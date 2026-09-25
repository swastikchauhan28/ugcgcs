package com.example.ugcgcs.domain.model

data class MissionWaypoint(
    val sequence: Int,
    val latitude: Double,
    val longitude: Double,
    val command: String = "WAYPOINT"
)

enum class FenceType { CIRCLE, POLYGON }

data class FenceConfig(
    val enabled: Boolean = true,
    val type: FenceType = FenceType.CIRCLE,
    val radiusMeters: Int = 180
)

data class MockParameter(
    val name: String,
    val value: String,
    val description: String
)

data class MavlinkMessage(
    val timestamp: Long,
    val name: String,
    val payload: String,
    val systemId: Int = 1,
    val componentId: Int = 1
)

enum class HealthStatus { HEALTHY, WARNING, ERROR }

data class HealthItem(val name: String, val status: HealthStatus, val detail: String)
