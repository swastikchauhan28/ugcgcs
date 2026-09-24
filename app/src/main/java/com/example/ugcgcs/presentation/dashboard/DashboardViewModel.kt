package com.example.ugcgcs.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ugcgcs.data.transport.TransportConfig
import com.example.ugcgcs.domain.model.ConnectionState
import com.example.ugcgcs.domain.model.VehicleState
import com.example.ugcgcs.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {
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
}
