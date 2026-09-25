package com.example.ugcgcs.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.ugcgcs.data.transport.TransportConfig
import com.example.ugcgcs.domain.model.ConnectionState
import com.example.ugcgcs.domain.repository.VehicleRepository
import com.example.ugcgcs.presentation.components.GcsKeyValue
import com.example.ugcgcs.presentation.components.GcsSection
import com.example.ugcgcs.presentation.components.PageHeading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private enum class TransportChoice { MOCK, UDP, TCP, SERIAL }

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: VehicleRepository) : ViewModel() {
    val vehicle = repository.vehicleState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), repository.vehicleState.value)
    fun connectMock() = viewModelScope.launch { repository.connect(TransportConfig.Mock) }
    fun disconnect() = viewModelScope.launch { repository.disconnect() }
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val vehicle by viewModel.vehicle.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf(TransportChoice.MOCK) }
    var host by remember { mutableStateOf("192.168.4.1") }
    var port by remember { mutableStateOf("14550") }
    Column(Modifier.fillMaxSize().background(Color(0xFF0A1015))) {
        PageHeading("Connection settings", "Only Mock transport is enabled in this prototype")
        GcsSection("Transport", Modifier.padding(12.dp)) {
            TransportChoice.entries.forEach { choice ->
                Row(Modifier.fillMaxWidth().clickable { selected = choice }.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selected == choice, onClick = { selected = choice })
                    Text(choice.name, color = Color(0xFFE7F0F5), modifier = Modifier.padding(start = 6.dp))
                    Spacer(Modifier.weight(1f))
                    Text(if (choice == TransportChoice.MOCK) "AVAILABLE" else "NOT IMPLEMENTED", color = if (choice == TransportChoice.MOCK) Color(0xFF83E8B3) else Color(0xFFFFD166), fontSize = 10.sp)
                }
            }
        }
        if (selected == TransportChoice.UDP || selected == TransportChoice.TCP) {
            GcsSection("Endpoint", Modifier.padding(horizontal = 12.dp)) {
                OutlinedTextField(host, { host = it }, label = { Text("IP address") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp))
                OutlinedTextField(port, { port = it }, label = { Text("Port") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp))
                Text("This transport is displayed for setup planning; it will not report a successful connection.", color = Color(0xFFFFD166), fontSize = 11.sp, modifier = Modifier.padding(12.dp))
            }
        }
        Button(
            onClick = if (vehicle.connectionState == ConnectionState.CONNECTED) viewModel::disconnect else viewModel::connectMock,
            enabled = selected == TransportChoice.MOCK || vehicle.connectionState == ConnectionState.CONNECTED,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) { Text(if (vehicle.connectionState == ConnectionState.CONNECTED) "DISCONNECT MOCK" else "CONNECT MOCK") }
        GcsSection("Current session", Modifier.padding(horizontal = 12.dp)) {
            GcsKeyValue("State", vehicle.connectionState.name)
            GcsKeyValue("Transport", vehicle.transportLabel)
            GcsKeyValue("Heartbeat", if (vehicle.lastHeartbeatTime == 0L) "Waiting" else "Live mock telemetry")
        }
    }
}
