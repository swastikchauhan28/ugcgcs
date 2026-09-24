package com.example.ugcgcs.navigation

enum class GcsDestination(val route: String, val label: String, val shortLabel: String) {
    DASHBOARD("dashboard", "Dashboard", "DASH"),
    MISSION("mission", "Mission", "MIS"),
    VEHICLE("vehicle", "Vehicle", "VEH"),
    TELEMETRY("telemetry", "Telemetry", "TEL"),
    PARAMETERS("parameters", "Parameters", "PAR"),
    SETTINGS("settings", "Settings", "SET")
}
