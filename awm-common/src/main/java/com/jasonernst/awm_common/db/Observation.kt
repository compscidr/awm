package com.jasonernst.awm_common.db

import kotlinx.serialization.Serializable

enum class ObservationType {
    BLE,
    WIFI,
    WIFI_DIRECT
}

@Serializable
sealed class Observation {
    abstract val id: Long
    abstract val timestampUTCMillis: Long
    abstract val location: Location?
    abstract val observationType: ObservationType?
    abstract val rssi: Int
    abstract val mac: String // bssid on wifi, address on ble
}