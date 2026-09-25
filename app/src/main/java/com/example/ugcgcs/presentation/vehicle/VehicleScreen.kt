package com.example.ugcgcs.presentation.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.ugcgcs.data.mock.MockGcsStore
import com.example.ugcgcs.domain.model.FenceType
import com.example.ugcgcs.domain.model.HealthItem
import com.example.ugcgcs.domain.model.HealthStatus
import com.example.ugcgcs.domain.model.VehicleCommand
import com.example.ugcgcs.domain.model.VehicleState
import com.example.ugcgcs.domain.repository.VehicleRepository
import com.example.ugcgcs.presentation.components.GcsKeyValue
import com.example.ugcgcs.presentation.components.GcsSection
import com.example.ugcgcs.presentation.components.PageHeading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val store: MockGcsStore,
    private val repository: VehicleRepository
) : ViewModel() {
    val vehicle = repository.vehicleState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), repository.vehicleState.value)
    val fence = store.fence.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), store.fence.value)
    fun stop() = viewModelScope.launch { repository.sendCommand(VehicleCommand.Stop) }
    fun toggleFence() = store.setFenceEnabled(!fence.value.enabled)
    fun fenceType(type: FenceType) = store.setFenceType(type)
}

@Composable
fun VehicleScreen(viewModel: VehicleViewModel = hiltViewModel()) {
    val vehicle by viewModel.vehicle.collectAsStateWithLifecycle()
    val fence by viewModel.fence.collectAsStateWithLifecycle()
    val health = vehicle.health(fence.enabled)
    Column(Modifier.fillMaxSize().background(Color(0xFF0A1015))) {
        PageHeading("Vehicle", "Mock Rover health and safety status")
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::stop, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B), contentColor = Color.Black), contentPadding = PaddingValues(vertical = 8.dp)) { Text("STOP / HOLD", fontWeight = FontWeight.Bold) }
            OutlinedButton(onClick = viewModel::toggleFence, modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 8.dp)) { Text(if (fence.enabled) "DISABLE FENCE" else "ENABLE FENCE", fontSize = 11.sp) }
        }
        GcsSection("Vehicle health", Modifier.padding(horizontal = 12.dp)) {
            health.forEach { item ->
                GcsKeyValue("${item.icon()} ${item.name}", item.detail)
            }
        }
        GcsSection("Geofence", Modifier.padding(12.dp)) {
            GcsKeyValue("Status", if (fence.enabled) vehicle.geofenceState.name else "DISABLED")
            GcsKeyValue("Shape", fence.type.name)
            GcsKeyValue("Radius", "${fence.radiusMeters} m")
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { viewModel.fenceType(FenceType.CIRCLE) }, modifier = Modifier.weight(1f)) { Text("CIRCLE") }
                OutlinedButton(onClick = { viewModel.fenceType(FenceType.POLYGON) }, modifier = Modifier.weight(1f)) { Text("POLYGON") }
            }
        }
    }
}

private fun VehicleState.health(fenceEnabled: Boolean) = listOf(
    HealthItem("GPS", HealthStatus.HEALTHY, "${gpsFix.label} • $satellites satellites"),
    HealthItem("EKF", HealthStatus.HEALTHY, "Position estimate stable"),
    HealthItem("Compass", HealthStatus.HEALTHY, "Heading ${heading.toInt()}°"),
    HealthItem("Battery", if (batteryPercentage < 25) HealthStatus.WARNING else HealthStatus.HEALTHY, "$batteryPercentage% • ${"%.1f".format(batteryVoltage)} V"),
    HealthItem("Radio", if (connectionState.name == "CONNECTED") HealthStatus.HEALTHY else HealthStatus.ERROR, connectionState.name),
    HealthItem("Geofence", if (fenceEnabled) HealthStatus.HEALTHY else HealthStatus.WARNING, if (fenceEnabled) geofenceState.name else "Disabled"),
    HealthItem("Position source", HealthStatus.HEALTHY, "GPS")
)

private fun HealthItem.icon() = when (status) { HealthStatus.HEALTHY -> "✓"; HealthStatus.WARNING -> "⚠"; HealthStatus.ERROR -> "✕" }
