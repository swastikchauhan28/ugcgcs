package com.example.ugcgcs.presentation.telemetry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.ugcgcs.data.mock.MockGcsStore
import com.example.ugcgcs.domain.repository.VehicleRepository
import com.example.ugcgcs.presentation.components.GcsKeyValue
import com.example.ugcgcs.presentation.components.GcsSection
import com.example.ugcgcs.presentation.components.PageHeading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TelemetryViewModel @Inject constructor(store: MockGcsStore, repository: VehicleRepository) : ViewModel() {
    val vehicle = repository.vehicleState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), repository.vehicleState.value)
    val messages = store.messages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), store.messages.value)
}

@Composable
fun TelemetryScreen(viewModel: TelemetryViewModel = hiltViewModel()) {
    val state by viewModel.vehicle.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize().background(Color(0xFF0A1015))) {
        item { PageHeading("Telemetry", "Live StateFlow mock telemetry • MAVLink inspector") }
        item {
            GcsSection("Position", Modifier.padding(12.dp)) {
                GcsKeyValue("Latitude", "%.6f".format(Locale.US, state.latitude)); GcsKeyValue("Longitude", "%.6f".format(Locale.US, state.longitude)); GcsKeyValue("Altitude", "%.1f m".format(Locale.US, state.altitude)); GcsKeyValue("Relative altitude", "%.1f m".format(Locale.US, state.relativeAltitude))
            }
            GcsSection("Orientation", Modifier.padding(horizontal = 12.dp)) {
                GcsKeyValue("Roll / Pitch", "%.1f° / %.1f°".format(Locale.US, state.roll, state.pitch)); GcsKeyValue("Yaw / Heading", "%.1f° / %.1f°".format(Locale.US, state.yaw, state.heading))
            }
            GcsSection("GPS / battery / vehicle", Modifier.padding(12.dp)) {
                GcsKeyValue("GPS", "${state.gpsFix.label} • ${state.satellites} SAT • HDOP ${"%.2f".format(state.gpsHdop)}"); GcsKeyValue("Battery", "${state.batteryPercentage}% • ${"%.2f".format(state.batteryVoltage)} V • ${"%.1f".format(state.batteryCurrent)} A"); GcsKeyValue("Vehicle", "SYS ${state.systemId} / COMP ${state.componentId} • ${state.flightMode} ${if (state.armed) "ARMED" else "DISARMED"}")
            }
            Text("MAVLINK INSPECTOR", color = Color(0xFF91A5B4), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        }
        items(messages, key = { it.timestamp }) { message ->
            Column(Modifier.padding(horizontal = 12.dp, vertical = 5.dp).background(Color(0xFF101A22)).padding(10.dp)) {
                Text("${SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(message.timestamp))}  ${message.name}", color = Color(0xFF70D6FF), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("SYS: ${message.systemId}  COMP: ${message.componentId}", color = Color(0xFF91A5B4), fontSize = 10.sp)
                Text(message.payload, color = Color(0xFFE7F0F5), fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(top = 3.dp))
            }
        }
    }
}
