package com.example.ugcgcs.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ugcgcs.data.transport.TransportConfig
import com.example.ugcgcs.domain.model.ConnectionState
import com.example.ugcgcs.domain.model.VehicleCommand
import com.example.ugcgcs.domain.model.VehicleMode
import com.example.ugcgcs.domain.model.VehicleState
import com.example.ugcgcs.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {
    private val _commandFeedback = MutableStateFlow("Mock vehicle connected. Commands stay inside the simulator.")
    val commandFeedback: StateFlow<String> = _commandFeedback.asStateFlow()

    val vehicleState: StateFlow<VehicleState> = repository.vehicleState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        repository.vehicleState.value
    )

    init { connectMock() }

    fun connectMock() = viewModelScope.launch {
        if (vehicleState.value.connectionState == ConnectionState.DISCONNECTED) {
            repository.connect(TransportConfig.Mock)
        }
    }

    fun disconnect() = viewModelScope.launch { repository.disconnect() }

    fun arm() = sendCommand(VehicleCommand.Arm, "Mock vehicle armed")
    fun disarm() = sendCommand(VehicleCommand.Disarm, "Mock vehicle disarmed")
    fun stop() = sendCommand(VehicleCommand.Stop, "STOP sent — mock vehicle is holding")
    fun setMode(mode: VehicleMode) = sendCommand(VehicleCommand.SetMode(mode), "Mode changed to ${mode.name}")

    private fun sendCommand(command: VehicleCommand, successMessage: String) = viewModelScope.launch {
        if (vehicleState.value.connectionState != ConnectionState.CONNECTED) {
            _commandFeedback.value = "Connect the mock vehicle before sending commands"
            return@launch
        }
        repository.sendCommand(command)
        _commandFeedback.value = successMessage
    }
}
