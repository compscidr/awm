package com.github.compscidr.awm.db

import kotlinx.serialization.Serializable

@Serializable
data class BLEObservation(
    override val id : Long = 0L,
    override val timestampUTCMillis: Long,
    override val location: Location,
    override val observationType: ObservationType = ObservationType.BLE,
    override val rssi: Int,
    override val mac: String, // bssid on wifi, address on ble
    val advertisingSid: Int,
    val dataStatus: Int, val advertisingInterval: Int,
    val primaryPhy: Int, val secondaryPhy: Int, val txPower: Int,
    val connectable: Boolean, val advertiseFlags: Int)
//    val adverisingMap: Map<Int, ByteArray>,
//    val rawAdvertisement: ByteArray, val manufacturerData: Map<Int, ByteArray>,
//    val serviceData: Map<@Contextual UUID, ByteArray>
    : Observation() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BLEObservation

        if (id != other.id) return false
        if (timestampUTCMillis != other.timestampUTCMillis) return false
        if (location != other.location) return false
        if (observationType != other.observationType) return false
        if (rssi != other.rssi) return false
        if (mac != other.mac) return false
        if (advertisingSid != other.advertisingSid) return false
        if (dataStatus != other.dataStatus) return false
        if (advertisingInterval != other.advertisingInterval) return false
        if (primaryPhy != other.primaryPhy) return false
        if (secondaryPhy != other.secondaryPhy) return false
        if (txPower != other.txPower) return false
        if (connectable != other.connectable) return false
        if (advertiseFlags != other.advertiseFlags) return false
//        if (adverisingMap != other.adverisingMap) return false
//        if (!rawAdvertisement.contentEquals(other.rawAdvertisement)) return false
//        if (manufacturerData != other.manufacturerData) return false
//        if (serviceData != other.serviceData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + timestampUTCMillis.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + observationType.hashCode()
        result = 31 * result + rssi
        result = 31 * result + mac.hashCode()
        result = 31 * result + advertisingSid
        result = 31 * result + dataStatus
        result = 31 * result + advertisingInterval
        result = 31 * result + primaryPhy
        result = 31 * result + secondaryPhy
        result = 31 * result + txPower
        result = 31 * result + connectable.hashCode()
        result = 31 * result + advertiseFlags
//        result = 31 * result + adverisingMap.hashCode()
//        result = 31 * result + rawAdvertisement.contentHashCode()
//        result = 31 * result + manufacturerData.hashCode()
//        result = 31 * result + serviceData.hashCode()
        return result
    }
}