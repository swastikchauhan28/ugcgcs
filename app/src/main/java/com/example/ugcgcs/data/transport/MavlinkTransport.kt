package com.example.ugcgcs.data.transport

/** Transport boundary. UDP, TCP and serial implementations belong behind this API in later phases. */
interface MavlinkTransport {
    suspend fun open(config: TransportConfig)
    suspend fun close()
}

sealed interface TransportConfig {
    data object Mock : TransportConfig
    data class Udp(val host: String, val port: Int) : TransportConfig
    data class Tcp(val host: String, val port: Int) : TransportConfig
    data class Serial(val device: String, val baudRate: Int) : TransportConfig
}
