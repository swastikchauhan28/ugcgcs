package com.example.ugcgcs.domain.model

sealed interface VehicleCommand {
    data object Arm : VehicleCommand
    data object Disarm : VehicleCommand
    data object Stop : VehicleCommand
    data class SetMode(val mode: VehicleMode) : VehicleCommand
    data class Velocity(val metersPerSecond: Double, val headingDegrees: Double) : VehicleCommand
}
