package com.example.ugcgcs.presentation.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ugcgcs.domain.model.ConnectionState
import com.example.ugcgcs.domain.model.VehicleMode
import com.example.ugcgcs.domain.model.VehicleState
import com.example.ugcgcs.presentation.components.TelemetryMetric
import java.util.Locale
import kotlin.math.max

private val Panel = Color(0xFF101A22)
private val MapBackground = Color(0xFF0D171E)
private val Grid = Color(0xFF1B333F)
private val Cyan = Color(0xFF70D6FF)
private val Green = Color(0xFF83E8B3)
private val Yellow = Color(0xFFFFD166)
private val Red = Color(0xFFFF6B6B)
private val Muted = Color(0xFF91A5B4)

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.vehicleState.collectAsStateWithLifecycle()
    val feedback by viewModel.commandFeedback.collectAsStateWithLifecycle()
    DashboardContent(
        state = state,
        feedback = feedback,
        onConnect = viewModel::connectMock,
        onDisconnect = viewModel::disconnect,
        onArm = viewModel::arm,
        onDisarm = viewModel::disarm,
        onStop = viewModel::stop,
        onSetMode = viewModel::setMode
    )
}

@Composable
private fun DashboardContent(
    state: VehicleState,
    feedback: String,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onArm: () -> Unit,
    onDisarm: () -> Unit,
    onStop: () -> Unit,
    onSetMode: (VehicleMode) -> Unit
) {
    Column(Modifier.fillMaxSize().background(Color(0xFF0A1015))) {
        StatusStrip(state, onConnect, onDisconnect)
        OperationalMap(state, Modifier.weight(1f))
        VehicleControls(state, feedback, onArm, onDisarm, onStop, onSetMode)
        HorizontalDivider(color = Grid)
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
        ConnectionState.CONNECTED -> Green
        ConnectionState.CONNECTING -> Yellow
        ConnectionState.DISCONNECTED -> Red
    }
    Row(
        modifier = Modifier.fillMaxWidth().background(Panel).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("●", color = statusColor, fontSize = 19.sp)
        Spacer(Modifier.width(7.dp))
        Text(state.connectionState.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        StatusDivider()
        Text("UGV-01", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        StatusDivider()
        Text(state.vehicleType.name, color = Muted, fontSize = 12.sp)
        StatusDivider()
        Text(state.flightMode.name, color = Cyan, fontWeight = FontWeight.Bold)
        StatusDivider()
        Text(if (state.armed) "ARMED" else "DISARMED", color = if (state.armed) Yellow else Muted, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Text("${state.transportLabel.uppercase()}  ", color = Muted, fontSize = 11.sp)
        Button(
            onClick = if (connected) onDisconnect else onConnect,
            colors = ButtonDefaults.buttonColors(containerColor = if (connected) Color(0xFF263B48) else Color(0xFF166B88)),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) { Text(if (connected) "DISCONNECT" else "CONNECT", fontSize = 11.sp) }
    }
}

@Composable
private fun StatusDivider() = Text("  |  ", color = Color(0xFF405460), fontSize = 12.sp)

@Composable
private fun OperationalMap(state: VehicleState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(MapBackground)
            .border(1.dp, Grid)
    ) {
        LocalMissionCanvas(state, Modifier.fillMaxSize())
        Column(Modifier.align(Alignment.TopStart).padding(14.dp)) {
            Text("LOCAL MISSION MAP", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("MOCK AREA • NAGPUR", color = Color(0xFF5A7180), fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp))
        }
        Column(Modifier.align(Alignment.TopEnd).padding(14.dp), horizontalAlignment = Alignment.End) {
            Text("HOME", color = Green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("${formatCoordinates(state.homeLatitude)}, ${formatCoordinates(state.homeLongitude)}", color = Muted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
        Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
            Text("GEOFENCE  ${state.geofenceState.name}   •   MISSION  ${state.missionState.name}", color = Muted, fontSize = 11.sp)
            Text("Vehicle movement begins after ARM + AUTO", color = Yellow, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
        }
        Text(
            "MOCK ONLY — NO REAL MAVLINK COMMANDS",
            color = Yellow,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomEnd).padding(14.dp)
        )
    }
}

@Composable
private fun LocalMissionCanvas(state: VehicleState, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val pad = 52f
        val left = pad
        val right = size.width - pad
        val top = pad
        val bottom = size.height - pad
        val width = max(1f, right - left)
        val height = max(1f, bottom - top)
        val minLat = 21.1447
        val maxLat = 21.1465
        val minLon = 79.0880
        val maxLon = 79.0900
        fun point(latitude: Double, longitude: Double) = Offset(
            x = left + (((longitude - minLon) / (maxLon - minLon)).toFloat() * width),
            y = bottom - (((latitude - minLat) / (maxLat - minLat)).toFloat() * height)
        )
        repeat(8) { step ->
            val x = left + width * step / 7f
            val y = top + height * step / 7f
            drawLine(Grid, Offset(x, top), Offset(x, bottom), 1f)
            drawLine(Grid, Offset(left, y), Offset(right, y), 1f)
        }
        val mission = listOf(
            21.1458 to 79.0882,
            21.14625 to 79.0890,
            21.1456 to 79.0898,
            21.14495 to 79.08905,
            21.1458 to 79.0882
        ).map { point(it.first, it.second) }
        mission.zipWithNext().forEach { (from, to) -> drawLine(Cyan.copy(alpha = .6f), from, to, 3f) }
        mission.dropLast(1).forEachIndexed { index, waypoint ->
            drawCircle(MapBackground, radius = 9f, center = waypoint)
            drawCircle(Cyan, radius = 7f, center = waypoint)
            drawCircle(MapBackground, radius = 2f, center = waypoint)
            if (index == 0) drawCircle(Green, radius = 12f, center = waypoint, style = Stroke(2f))
        }
        val vehicle = point(state.latitude, state.longitude)
        drawCircle(Cyan.copy(alpha = .16f), radius = 26f, center = vehicle)
        rotate(state.heading.toFloat(), vehicle) {
            val marker = Path().apply {
                moveTo(vehicle.x, vehicle.y - 17f)
                lineTo(vehicle.x - 12f, vehicle.y + 13f)
                lineTo(vehicle.x + 12f, vehicle.y + 13f)
                close()
            }
            drawPath(marker, if (state.armed) Yellow else Cyan)
        }
    }
}

@Composable
private fun VehicleControls(
    state: VehicleState,
    feedback: String,
    onArm: () -> Unit,
    onDisarm: () -> Unit,
    onStop: () -> Unit,
    onSetMode: (VehicleMode) -> Unit
) {
    var showArmConfirmation by remember { mutableStateOf(false) }
    val enabled = state.connectionState == ConnectionState.CONNECTED
    Column(Modifier.fillMaxWidth().background(Panel).padding(horizontal = 12.dp, vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("VEHICLE CONTROL", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(12.dp))
            Text(feedback, color = Cyan, fontSize = 11.sp)
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { showArmConfirmation = true },
                enabled = enabled && !state.armed,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 7.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF246B4E))
            ) { Text("ARM", fontSize = 11.sp) }
            OutlinedButton(
                onClick = onDisarm,
                enabled = enabled && state.armed,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 7.dp),
                border = BorderStroke(1.dp, Yellow)
            ) { Text("DISARM", color = Yellow, fontSize = 11.sp) }
            Button(
                onClick = onStop,
                enabled = enabled,
                modifier = Modifier.weight(1.2f),
                contentPadding = PaddingValues(vertical = 7.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red, contentColor = Color.Black)
            ) { Text("STOP / HOLD", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
        }
        Row(Modifier.fillMaxWidth().padding(top = 7.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(VehicleMode.HOLD, VehicleMode.GUIDED, VehicleMode.AUTO, VehicleMode.RTL).forEach { mode ->
                OutlinedButton(
                    onClick = { onSetMode(mode) },
                    enabled = enabled,
                    modifier = Modifier.weight(1f).height(34.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                    border = BorderStroke(1.dp, if (state.flightMode == mode) Cyan else Grid)
                ) { Text(mode.name, color = if (state.flightMode == mode) Cyan else Muted, fontSize = 10.sp) }
            }
        }
    }
    if (showArmConfirmation) {
        AlertDialog(
            onDismissRequest = { showArmConfirmation = false },
            title = { Text("Arm mock vehicle?") },
            text = { Text("This changes only the local mock simulation. No MAVLink packet or real-vehicle command will be sent.") },
            confirmButton = {
                TextButton(onClick = { onArm(); showArmConfirmation = false }) { Text("ARM MOCK", color = Yellow) }
            },
            dismissButton = { TextButton(onClick = { showArmConfirmation = false }) { Text("CANCEL") } }
        )
    }
}

private fun format(value: Double) = "%.1f".format(Locale.US, value)
private fun formatCoordinates(value: Double) = "%.5f".format(Locale.US, value)
