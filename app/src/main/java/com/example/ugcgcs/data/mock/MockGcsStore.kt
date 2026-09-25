package com.example.ugcgcs.data.mock

import com.example.ugcgcs.domain.model.FenceConfig
import com.example.ugcgcs.domain.model.FenceType
import com.example.ugcgcs.domain.model.MavlinkMessage
import com.example.ugcgcs.domain.model.MissionWaypoint
import com.example.ugcgcs.domain.model.MockParameter
import com.example.ugcgcs.domain.model.VehicleState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** In-memory data used by prototype screens until MAVLink mission/parameter services are added. */
@Singleton
class MockGcsStore @Inject constructor() {
    private val _mission = MutableStateFlow(defaultMission())
    val mission: StateFlow<List<MissionWaypoint>> = _mission.asStateFlow()

    private val _fence = MutableStateFlow(FenceConfig())
    val fence: StateFlow<FenceConfig> = _fence.asStateFlow()

    private val _parameters = MutableStateFlow(defaultParameters())
    val parameters: StateFlow<List<MockParameter>> = _parameters.asStateFlow()

    private val _messages = MutableStateFlow<List<MavlinkMessage>>(emptyList())
    val messages: StateFlow<List<MavlinkMessage>> = _messages.asStateFlow()

    fun addWaypoint() {
        val last = _mission.value.lastOrNull()
        val latitude = (last?.latitude ?: 21.1458) + 0.00025
        val longitude = (last?.longitude ?: 79.0882) + 0.00018
        _mission.value = (_mission.value + MissionWaypoint(_mission.value.size + 1, latitude, longitude)).resequenced()
    }

    fun removeWaypoint(index: Int) {
        _mission.value = _mission.value.filterIndexed { itemIndex, _ -> itemIndex != index }.resequenced()
    }

    fun moveWaypoint(index: Int, offset: Int) {
        val target = index + offset
        if (target !in _mission.value.indices) return
        val updated = _mission.value.toMutableList()
        val item = updated.removeAt(index)
        updated.add(target, item)
        _mission.value = updated.resequenced()
    }

    fun clearMission() { _mission.value = emptyList() }
    fun setFenceEnabled(enabled: Boolean) { _fence.value = _fence.value.copy(enabled = enabled) }
    fun setFenceType(type: FenceType) { _fence.value = _fence.value.copy(type = type) }
    fun updateRadius(radius: Int) { _fence.value = _fence.value.copy(radiusMeters = radius.coerceIn(30, 500)) }

    fun updateParameter(name: String, value: String) {
        _parameters.value = _parameters.value.map { if (it.name == name) it.copy(value = value) else it }
    }

    fun record(name: String, payload: String, state: VehicleState? = null) {
        val message = MavlinkMessage(
            timestamp = System.currentTimeMillis(),
            name = name,
            payload = payload,
            systemId = state?.systemId ?: 1,
            componentId = state?.componentId ?: 1
        )
        _messages.value = (listOf(message) + _messages.value).take(80)
    }

    private fun List<MissionWaypoint>.resequenced() = mapIndexed { index, waypoint -> waypoint.copy(sequence = index + 1) }

    private fun defaultMission() = listOf(
        MissionWaypoint(1, 21.1458, 79.0882),
        MissionWaypoint(2, 21.14625, 79.0890),
        MissionWaypoint(3, 21.1456, 79.0898),
        MissionWaypoint(4, 21.14495, 79.08905)
    )

    private fun defaultParameters() = listOf(
        MockParameter("FENCE_ENABLE", "1", "Enable the Rover safety fence."),
        MockParameter("FENCE_ACTION", "1", "Action taken after a fence breach."),
        MockParameter("FENCE_MARGIN", "5.0", "Fence margin in metres."),
        MockParameter("EK3_SRC1_POSXY", "0", "EKF3 horizontal position source."),
        MockParameter("EK3_SRC1_VELXY", "0", "EKF3 horizontal velocity source."),
        MockParameter("VISO_TYPE", "0", "Visual odometry sensor type."),
        MockParameter("SERIAL1_PROTOCOL", "2", "Telemetry serial protocol."),
        MockParameter("SERIAL1_BAUD", "57", "Telemetry serial baud rate preset.")
    )
}
