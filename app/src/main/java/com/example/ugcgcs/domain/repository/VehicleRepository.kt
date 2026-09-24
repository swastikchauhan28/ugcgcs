package com.example.ugcgcs.domain.repository

import com.example.ugcgcs.data.transport.TransportConfig
import com.example.ugcgcs.domain.model.VehicleCommand
import com.example.ugcgcs.domain.model.VehicleMode
import com.example.ugcgcs.domain.model.VehicleState
import kotlinx.coroutines.flow.StateFlow

interface VehicleRepository {
    val vehicleState: StateFlow<VehicleState>
    suspend fun connect(config: TransportConfig = TransportConfig.Mock)
    suspend fun disconnect()
    suspend fun sendCommand(command: VehicleCommand)
    suspend fun setMode(mode: VehicleMode)
}
