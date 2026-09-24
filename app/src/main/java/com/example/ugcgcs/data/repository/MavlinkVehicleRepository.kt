package com.example.ugcgcs.data.repository

import com.example.ugcgcs.data.mavlink.MavlinkConnection
import com.example.ugcgcs.data.transport.TransportConfig
import com.example.ugcgcs.domain.model.VehicleCommand
import com.example.ugcgcs.domain.model.VehicleMode
import com.example.ugcgcs.domain.model.VehicleState
import com.example.ugcgcs.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MavlinkVehicleRepository @Inject constructor(
    private val connection: MavlinkConnection
) : VehicleRepository {
    override val vehicleState: StateFlow<VehicleState> = connection.vehicleState
    override suspend fun connect(config: TransportConfig) = connection.connect(config)
    override suspend fun disconnect() = connection.disconnect()
    override suspend fun sendCommand(command: VehicleCommand) = connection.sendCommand(command)
    override suspend fun setMode(mode: VehicleMode) = connection.setMode(mode)
}
