package com.example.ugcgcs.domain.model

enum class ConnectionState { DISCONNECTED, CONNECTING, CONNECTED }

enum class VehicleMode { MANUAL, HOLD, GUIDED, AUTO, RTL, UNKNOWN }

enum class GeofenceState { SAFE, WARNING, BREACHED, UNKNOWN }

enum class GpsFix(val label: String) { NO_FIX("No fix"), FIX_2D("2D"), FIX_3D("3D"), RTK("RTK") }

enum class MissionState { IDLE, UPLOADED, RUNNING, PAUSED, COMPLETE }

enum class VehicleType { ROVER, UNKNOWN }

data class VehicleState(
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val systemId: Int = 1,
    val componentId: Int = 1,
    val latitude: Double = 21.1458,
    val longitude: Double = 79.0882,
    val altitude: Double = 312.4,
    val relativeAltitude: Double = 0.0,
    val speed: Double = 0.0,
    val heading: Double = 84.0,
    val roll: Double = 0.0,
    val pitch: Double = 0.0,
    val yaw: Double = 84.0,
    val batteryPercentage: Int = 100,
    val batteryVoltage: Double = 12.6,
    val batteryCurrent: Double = 0.0,
    val armed: Boolean = false,
    val flightMode: VehicleMode = VehicleMode.HOLD,
    val gpsFix: GpsFix = GpsFix.FIX_3D,
    val satellites: Int = 14,
    val gpsHdop: Double = 0.8,
    val homeLatitude: Double = 21.1458,
    val homeLongitude: Double = 79.0882,
    val missionState: MissionState = MissionState.IDLE,
    val geofenceState: GeofenceState = GeofenceState.SAFE,
    val lastHeartbeatTime: Long = 0L,
    val vehicleType: VehicleType = VehicleType.ROVER,
    val transportLabel: String = "Mock"
)
