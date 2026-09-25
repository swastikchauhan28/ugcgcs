package com.example.ugcgcs.presentation.mission

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.ugcgcs.data.mock.MockGcsStore
import com.example.ugcgcs.domain.model.VehicleMode
import com.example.ugcgcs.domain.repository.VehicleRepository
import com.example.ugcgcs.presentation.components.GcsSection
import com.example.ugcgcs.presentation.components.PageHeading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val store: MockGcsStore,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {
    val mission = store.mission.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), store.mission.value)
    val vehicle = vehicleRepository.vehicleState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), vehicleRepository.vehicleState.value)
    fun add() = store.addWaypoint()
    fun remove(index: Int) = store.removeWaypoint(index)
    fun move(index: Int, direction: Int) = store.moveWaypoint(index, direction)
    fun clear() = store.clearMission()
    fun upload() { store.record("MISSION_COUNT", "count: ${mission.value.size}", vehicle.value) }
    fun start() = viewModelScope.launch { vehicleRepository.setMode(VehicleMode.AUTO); store.record("MISSION_START", "AUTO requested", vehicle.value) }
    fun pause() = viewModelScope.launch { vehicleRepository.setMode(VehicleMode.HOLD); store.record("MISSION_PAUSE", "HOLD requested", vehicle.value) }
}

@Composable
fun MissionScreen(viewModel: MissionViewModel = hiltViewModel()) {
    val mission by viewModel.mission.collectAsStateWithLifecycle()
    val vehicle by viewModel.vehicle.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().background(Color(0xFF0A1015))) {
        PageHeading("Mission planner", "Local in-memory waypoints • upload is mock-only")
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Button(onClick = viewModel::add, modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 7.dp)) { Text("ADD", fontSize = 11.sp) }
            OutlinedButton(onClick = viewModel::clear, modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 7.dp)) { Text("CLEAR", fontSize = 11.sp) }
            Button(onClick = viewModel::upload, modifier = Modifier.weight(1.2f), contentPadding = PaddingValues(vertical = 7.dp)) { Text("UPLOAD", fontSize = 11.sp) }
            Button(onClick = if (vehicle.flightMode == VehicleMode.AUTO) viewModel::pause else viewModel::start, modifier = Modifier.weight(1.2f), contentPadding = PaddingValues(vertical = 7.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF166B88))) {
                Text(if (vehicle.flightMode == VehicleMode.AUTO) "PAUSE" else "START", fontSize = 11.sp)
            }
        }
        GcsSection("Mission items", Modifier.padding(horizontal = 12.dp)) {
            if (mission.isEmpty()) Text("No waypoints. Add a waypoint to create a local mission.", color = Color(0xFF91A5B4), modifier = Modifier.padding(12.dp))
            else LazyColumn {
                itemsIndexed(mission, key = { _, waypoint -> waypoint.sequence }) { index, waypoint ->
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 9.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${waypoint.sequence}", color = Color(0xFF70D6FF), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Column(Modifier.weight(1f)) {
                            Text(waypoint.command, color = Color(0xFFE7F0F5), fontSize = 12.sp)
                            Text("${waypoint.latitude.format5()}, ${waypoint.longitude.format5()}", color = Color(0xFF91A5B4), fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }
                        Text("↑", color = Color(0xFF70D6FF), modifier = Modifier.padding(4.dp).clickable { viewModel.move(index, -1) })
                        Text("↓", color = Color(0xFF70D6FF), modifier = Modifier.padding(4.dp).clickable { viewModel.move(index, 1) })
                        Text("×", color = Color(0xFFFF6B6B), modifier = Modifier.padding(4.dp).clickable { viewModel.remove(index) })
                    }
                }
            }
        }
    }
}

private fun Double.format5() = "%.5f".format(Locale.US, this)
