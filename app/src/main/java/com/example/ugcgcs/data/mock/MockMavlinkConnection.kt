package com.example.ugcgcs.data.mock

import com.example.ugcgcs.data.mavlink.MavlinkConnection
import com.example.ugcgcs.data.transport.TransportConfig
import com.example.ugcgcs.domain.model.ConnectionState
import com.example.ugcgcs.domain.model.MissionState
import com.example.ugcgcs.domain.model.VehicleCommand
import com.example.ugcgcs.domain.model.VehicleMode
import com.example.ugcgcs.domain.model.VehicleState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.sin

@Singleton
class MockMavlinkConnection @Inject constructor() : MavlinkConnection {
    private val _vehicleState = MutableStateFlow(VehicleState())
    override val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()
    private var simulationJob: Job? = null
    private val scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Default)
    private var routeIndex = 0
    private var batteryRemaining = 100.0

    private val route = listOf(
        21.1458 to 79.0882,
        21.14625 to 79.0890,
        21.1456 to 79.0898,
        21.14495 to 79.08905,
        21.1458 to 79.0882
    )

    override suspend fun connect(config: TransportConfig) {
        require(config is TransportConfig.Mock) { "Real transports are not implemented in this prototype." }
        _vehicleState.value = _vehicleState.value.copy(connectionState = ConnectionState.CONNECTING)
        delay(350)
        _vehicleState.value = _vehicleState.value.copy(
            connectionState = ConnectionState.CONNECTED,
            lastHeartbeatTime = System.currentTimeMillis(),
            batteryPercentage = batteryRemaining.toInt(),
            transportLabel = "Mock simulation"
        )
        startSimulation()
    }

    override suspend fun disconnect() {
        simulationJob?.cancel()
        simulationJob = null
        _vehicleState.value = _vehicleState.value.copy(
            connectionState = ConnectionState.DISCONNECTED,
            armed = false,
            speed = 0.0,
            flightMode = VehicleMode.HOLD
        )
    }

    override suspend fun sendCommand(command: VehicleCommand) {
        when (command) {
            VehicleCommand.Arm -> arm()
            VehicleCommand.Disarm -> disarm()
            VehicleCommand.Stop -> _vehicleState.value = _vehicleState.value.copy(speed = 0.0, flightMode = VehicleMode.HOLD)
            is VehicleCommand.SetMode -> setMode(command.mode)
            is VehicleCommand.Velocity -> sendVelocityCommand(command.metersPerSecond, command.headingDegrees)
        }
    }

    override suspend fun setMode(mode: VehicleMode) {
        _vehicleState.value = _vehicleState.value.copy(
            flightMode = mode,
            missionState = if (mode == VehicleMode.AUTO) MissionState.RUNNING else _vehicleState.value.missionState
        )
    }

    override suspend fun arm() { _vehicleState.value = _vehicleState.value.copy(armed = true) }
    override suspend fun disarm() { _vehicleState.value = _vehicleState.value.copy(armed = false, speed = 0.0) }

    override suspend fun sendVelocityCommand(metersPerSecond: Double, headingDegrees: Double) {
        _vehicleState.value = _vehicleState.value.copy(
            speed = metersPerSecond.coerceIn(0.0, 5.0),
            heading = headingDegrees.mod(360.0),
            yaw = headingDegrees.mod(360.0),
            flightMode = VehicleMode.GUIDED
        )
    }

    override suspend fun uploadMission() {
        _vehicleState.value = _vehicleState.value.copy(missionState = MissionState.UPLOADED)
    }

    override suspend fun downloadParameters() = Unit

    private fun startSimulation() {
        if (simulationJob?.isActive == true) return
        simulationJob = scope.launch {
            while (isActive) {
                delay(1_000)
                val current = _vehicleState.value
                if (current.connectionState != ConnectionState.CONNECTED) continue
                val runningAuto = current.armed && current.flightMode == VehicleMode.AUTO
                val next = if (runningAuto) moveTowardsNextWaypoint(current) else current.copy(speed = current.speed * 0.65)
                val drain = if (runningAuto) 0.04 else 0.005
                batteryRemaining = (batteryRemaining - drain).coerceAtLeast(0.0)
                _vehicleState.value = next.copy(
                    batteryPercentage = batteryRemaining.toInt(),
                    batteryVoltage = (12.6 - (100 - batteryRemaining) * 0.012).coerceAtLeast(10.8),
                    batteryCurrent = if (runningAuto) 2.1 else 0.35,
                    satellites = 13 + ((System.currentTimeMillis() / 5_000) % 3).toInt(),
                    gpsHdop = 0.75 + ((System.currentTimeMillis() / 3_000) % 4) * 0.03,
                    lastHeartbeatTime = System.currentTimeMillis()
                )
            }
        }
    }

    private fun moveTowardsNextWaypoint(current: VehicleState): VehicleState {
        val target = route[(routeIndex + 1) % route.size()]
        val latDelta = target.first - current.latitude
        val lonDelta = target.second - current.longitude
        val distance = kotlin.math.hypot(latDelta, lonDelta)
        if (distance < 0.00005) routeIndex = (routeIndex + 1) % route.size()
        val heading = Math.toDegrees(atan2(lonDelta, latDelta)).mod(360.0)
        val step = 0.00004
        val scale = (step / distance).coerceAtMost(1.0)
        return current.copy(
            latitude = current.latitude + latDelta * scale,
            longitude = current.longitude + lonDelta * scale,
            speed = 1.8 + sin(System.currentTimeMillis() / 1_500.0) * 0.2,
            heading = heading,
            yaw = heading
        )
    }
}
