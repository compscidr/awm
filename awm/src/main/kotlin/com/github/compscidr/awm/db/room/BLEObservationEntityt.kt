package com.github.compscidr.awm.db.room

import android.bluetooth.le.ScanResult
import android.os.Build
import androidx.room.Entity
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

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

// https://developer.android.com/reference/android/bluetooth/le/ScanResult
// https://developer.android.com/reference/android/bluetooth/BluetoothDevice
// https://developer.android.com/reference/android/bluetooth/le/ScanRecord

// for the list of UUIDs we should probably do something like this: https://stackoverflow.com/a/64242694
// however, I don't expect anyone will really be interested in querying it, so not bothering with it for now
@Entity
class BLEObservationEntity(id: Long, timestampUTCMillis: Long, locationEntity: LocationEntity, observationType: ObservationType, rssi: Int, mac: String,
                           val advertisingSid: Int, val dataStatus: Int, val advertisingInterval: Int,
                           val primaryPhy: Int, val secondaryPhy: Int, val txPower: Int,
                           val connectable: Boolean, val advertiseFlags: Int)
//, val adverisingMap: Map<Int, ByteArray>,
//                           val rawAdvertisement: ByteArray, val manufacturerData: Map<Int, ByteArray>,
//                           val serviceData: Map<UUID, ByteArray>)
    : ObservationEntity(timestampUTCMillis = timestampUTCMillis, locationEntity = locationEntity, observationType = ObservationType.BLE, rssi = rssi, mac = mac) {
    companion object {
        fun fromScanResultAndLocation(result: ScanResult, location: android.location.Location): BLEObservationEntity {
            val time = System.currentTimeMillis()
            val storageLocationEntity = LocationEntity.fromAndroidLocation(location)

            var advertisingSid = 0
            var dataStatus = 0
            var advertingInterval = 0
            var primaryPhy = 0
            var secondaryPhy = 0
            var txPower = 0
            var connectable = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                advertisingSid = result.advertisingSid
                dataStatus = result.dataStatus
                advertingInterval = result.periodicAdvertisingInterval
                primaryPhy = result.primaryPhy
                secondaryPhy = result.secondaryPhy
                txPower = result.txPower
                connectable = result.isConnectable
            }
            val scanRecord = result.scanRecord
//            var advertisingMap = emptyMap<Int, ByteArray>()
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
//                advertisingMap = scanRecord?.advertisingDataMap ?: emptyMap()
//            }
//            val manufacturingData = HashMap<Int, ByteArray>()
//            var i = 0
//            while (i < (scanRecord?.manufacturerSpecificData?.size() ?: 0)) {
//                manufacturingData[scanRecord?.manufacturerSpecificData?.keyAt(i) ?: 0] = scanRecord?.manufacturerSpecificData?.valueAt(i) ?: ByteArray(0)
//                i++
//            }
//            val serviceData = HashMap<UUID, ByteArray>()
//            for (entry in (scanRecord?.serviceData ?: emptyMap())) {
//                serviceData[entry.key.uuid] = entry.value
//            }

            return BLEObservationEntity(0L, time, storageLocationEntity, ObservationType.BLE, result.rssi, result.device.address, advertisingSid, dataStatus, advertingInterval, primaryPhy, secondaryPhy, txPower, connectable,
                scanRecord?.advertiseFlags ?: 0, ) //advertisingMap, scanRecord?.bytes ?: ByteArray(0), manufacturingData, serviceData)
        }

        fun toBLEObservation(bleObservationEntity: BLEObservationEntity): BLEObservation {
            return BLEObservation(bleObservationEntity.id, bleObservationEntity.timestampUTCMillis, LocationEntity.toLocation(bleObservationEntity.locationEntity), ObservationType.BLE, bleObservationEntity.rssi, bleObservationEntity.mac, bleObservationEntity.advertisingSid,
                bleObservationEntity.dataStatus, bleObservationEntity.advertisingInterval, bleObservationEntity.primaryPhy, bleObservationEntity.secondaryPhy, bleObservationEntity.txPower, bleObservationEntity.connectable,
                bleObservationEntity.advertiseFlags) //, bleObservationEntity.adverisingMap, bleObservationEntity.rawAdvertisement, bleObservationEntity.manufacturerData, bleObservationEntity.serviceData)
        }
    }
}