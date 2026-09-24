package com.example.ugcgcs.data.mavlink

import com.example.ugcgcs.data.transport.TransportConfig
import com.example.ugcgcs.domain.model.VehicleCommand
import com.example.ugcgcs.domain.model.VehicleMode
import com.example.ugcgcs.domain.model.VehicleState
import kotlinx.coroutines.flow.StateFlow

/** UI-independent contract for a future MAVLink 2 implementation. */
interface MavlinkConnection {
    val vehicleState: StateFlow<VehicleState>
    suspend fun connect(config: TransportConfig)
    suspend fun disconnect()
    suspend fun sendCommand(command: VehicleCommand)
    suspend fun setMode(mode: VehicleMode)
    suspend fun arm()
    suspend fun disarm()
    suspend fun sendVelocityCommand(metersPerSecond: Double, headingDegrees: Double)
    suspend fun uploadMission()
    suspend fun downloadParameters()
}
