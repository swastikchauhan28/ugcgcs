package com.example.ugcgcs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ugcgcs.navigation.GcsDestination
import com.example.ugcgcs.presentation.dashboard.DashboardScreen
import com.example.ugcgcs.presentation.mission.MissionScreen
import com.example.ugcgcs.presentation.parameters.ParametersScreen
import com.example.ugcgcs.presentation.settings.SettingsScreen
import com.example.ugcgcs.presentation.telemetry.TelemetryScreen
import com.example.ugcgcs.presentation.theme.UgcGcsTheme
import com.example.ugcgcs.presentation.vehicle.VehicleScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UgcGcsTheme { GcsApp() } }
    }
}

@Composable
private fun GcsApp() {
    val navController = rememberNavController()
    val backStack = navController.currentBackStackEntryAsState()
    val currentRoute = backStack.value?.destination?.route
    val destinations = GcsDestination.entries

    Column(Modifier.fillMaxSize().background(Color(0xFF0A1015))) {
        NavHost(
            navController = navController,
            startDestination = GcsDestination.DASHBOARD.route,
            modifier = Modifier.weight(1f)
        ) {
            composable(GcsDestination.DASHBOARD.route) { DashboardScreen() }
            composable(GcsDestination.MISSION.route) { MissionScreen() }
            composable(GcsDestination.VEHICLE.route) { VehicleScreen() }
            composable(GcsDestination.TELEMETRY.route) { TelemetryScreen() }
            composable(GcsDestination.PARAMETERS.route) { ParametersScreen() }
            composable(GcsDestination.SETTINGS.route) { SettingsScreen() }
        }
        NavigationBar(containerColor = Color(0xFF101A22), modifier = Modifier.fillMaxWidth()) {
            destinations.forEach { destination ->
                NavigationBarItem(
                    selected = currentRoute == destination.route,
                    onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text(destination.shortLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    label = { Text(destination.label, fontSize = 10.sp) }
                )
            }
        }
    }
}
