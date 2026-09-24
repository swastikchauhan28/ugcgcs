package com.example.ugcgcs.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ugcgcs.domain.model.ConnectionState
import com.example.ugcgcs.domain.model.VehicleState
import com.example.ugcgcs.presentation.components.TelemetryMetric
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.vehicleState.collectAsStateWithLifecycle()
    DashboardContent(
        state = state,
        onConnect = viewModel::connectMock,
        onDisconnect = viewModel::disconnect
    )
}

@Composable
private fun DashboardContent(
    state: VehicleState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Color(0xFF0A1015))) {
        StatusStrip(state, onConnect, onDisconnect)
        OperationalArea(state, Modifier.weight(1f))
        HorizontalDivider(color = Color(0xFF29404D))
        Row(Modifier.fillMaxWidth()) {
            TelemetryMetric("GPS", "${state.gpsFix.label} / HDOP ${format(state.gpsHdop)}", Modifier.weight(1.25f))
            TelemetryMetric("SAT", state.satellites.toString(), Modifier.weight(.58f))
            TelemetryMetric("SPEED", "${format(state.speed)} m/s", Modifier.weight(.9f))
            TelemetryMetric("BATTERY", "${state.batteryPercentage}%", Modifier.weight(.82f))
            TelemetryMetric("ALT", "${format(state.relativeAltitude)} m", Modifier.weight(.72f))
            TelemetryMetric("HDG", "%03.0f°".format(Locale.US, state.heading), Modifier.weight(.65f))
        }
    }
}

@Composable
private fun StatusStrip(state: VehicleState, onConnect: () -> Unit, onDisconnect: () -> Unit) {
    val connected = state.connectionState == ConnectionState.CONNECTED
    val statusColor = when (state.connectionState) {
        ConnectionState.CONNECTED -> Color(0xFF83E8B3)
        ConnectionState.CONNECTING -> Color(0xFFFFD166)
        ConnectionState.DISCONNECTED -> Color(0xFFFF6B6B)
    }
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF101A22)).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("●", color = statusColor, fontSize = 19.sp)
        Spacer(Modifier.width(7.dp))
        Text(state.connectionState.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        StatusDivider()
        Text("UGV-01", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        StatusDivider()
        Text(state.vehicleType.name, color = Color(0xFF91A5B4), fontSize = 12.sp)
        StatusDivider()
        Text(state.flightMode.name, color = Color(0xFF70D6FF), fontWeight = FontWeight.Bold)
        StatusDivider()
        Text(if (state.armed) "ARMED" else "DISARMED", color = if (state.armed) Color(0xFFFFD166) else Color(0xFF91A5B4), fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Text("${state.transportLabel.uppercase()}  ", color = Color(0xFF91A5B4), fontSize = 11.sp)
        Button(
            onClick = if (connected) onDisconnect else onConnect,
            colors = ButtonDefaults.buttonColors(containerColor = if (connected) Color(0xFF263B48) else Color(0xFF166B88)),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) { Text(if (connected) "DISCONNECT" else "CONNECT", fontSize = 11.sp) }
    }
}

@Composable
private fun StatusDivider() = Text("  |  ", color = Color(0xFF405460), fontSize = 12.sp)

@Composable
private fun OperationalArea(state: VehicleState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(16.dp).border(BorderStroke(1.dp, Color(0xFF29404D)), Color(0xFF0D171E))
    ) {
        Column(Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Text("OPERATIONAL AREA", color = Color(0xFF91A5B4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("MAP INTEGRATION — PHASE 3", color = Color(0xFF5A7180), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("▲", color = Color(0xFF70D6FF), fontSize = 60.sp, modifier = Modifier.rotate(state.heading.toFloat()))
            Text("UGV-01", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            Text("${formatCoordinates(state.latitude)}, ${formatCoordinates(state.longitude)}", color = Color(0xFF91A5B4), fontSize = 12.sp)
        }
        Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text("HOME  ${formatCoordinates(state.homeLatitude)}, ${formatCoordinates(state.homeLongitude)}", color = Color(0xFF83E8B3), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Text("GEOFENCE  ${state.geofenceState.name}   •   MISSION  ${state.missionState.name}", color = Color(0xFF91A5B4), fontSize = 11.sp, modifier = Modifier.padding(top = 5.dp))
        }
        Text("MOCK VEHICLE — NO REAL MAVLINK COMMANDS", color = Color(0xFFFFD166), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp))
    }
}

private fun format(value: Double) = "%.1f".format(Locale.US, value)
private fun formatCoordinates(value: Double) = "%.5f".format(Locale.US, value)
