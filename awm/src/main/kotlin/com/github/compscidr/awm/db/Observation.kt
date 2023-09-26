package com.github.compscidr.awm.db

enum class ObservationType {
    BLE,
    WIFI,
    WIFI_DIRECT
}

sealed class Observation {
    open val id: Long = 0L
    open val timestampUTCMillis: Long = 0L
    open val location: Location? = null
    open val observationType: ObservationType? = null
    open val rssi: Int = 0
    open val mac: String = "" // bssid on wifi, address on ble
}