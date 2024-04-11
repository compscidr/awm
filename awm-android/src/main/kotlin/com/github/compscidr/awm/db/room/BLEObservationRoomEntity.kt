package com.github.compscidr.awm.db.room

import android.bluetooth.le.ScanResult
import android.os.Build
import androidx.room.Entity
import com.jasonernst.awm_common.db.BLEObservation
import com.jasonernst.awm_common.db.ObservationType

// https://developer.android.com/reference/android/bluetooth/le/ScanResult
// https://developer.android.com/reference/android/bluetooth/BluetoothDevice
// https://developer.android.com/reference/android/bluetooth/le/ScanRecord

// for the list of UUIDs we should probably do something like this: https://stackoverflow.com/a/64242694
// however, I don't expect anyone will really be interested in querying it, so not bothering with it for now
@Entity
class BLEObservationRoomEntity(id: Long, timestampUTCMillis: Long, locationRoomEntity: LocationRoomEntity, rssi: Int, mac: String,
                               val advertisingSid: Int, val dataStatus: Int, val advertisingInterval: Int,
                               val primaryPhy: Int, val secondaryPhy: Int, val txPower: Int,
                               val connectable: Boolean, val advertiseFlags: Int)
//, val adverisingMap: Map<Int, ByteArray>,
//                           val rawAdvertisement: ByteArray, val manufacturerData: Map<Int, ByteArray>,
//                           val serviceData: Map<UUID, ByteArray>)
    : ObservationRoomEntity(id = id, timestampUTCMillis = timestampUTCMillis, locationRoomEntity = locationRoomEntity, observationType = ObservationType.BLE, rssi = rssi, mac = mac) {
    companion object {
        fun fromScanResultAndLocation(result: ScanResult, location: android.location.Location): BLEObservationRoomEntity {
            val time = System.currentTimeMillis()
            val storageLocationRoomEntity = LocationRoomEntity.fromAndroidLocation(location)

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

            return BLEObservationRoomEntity(0L, time, storageLocationRoomEntity, result.rssi, result.device.address, advertisingSid, dataStatus, advertingInterval, primaryPhy, secondaryPhy, txPower, connectable,
                scanRecord?.advertiseFlags ?: 0, ) //advertisingMap, scanRecord?.bytes ?: ByteArray(0), manufacturingData, serviceData)
        }

        fun toBLEObservation(bleObservationRoomEntity: BLEObservationRoomEntity): BLEObservation {
            return BLEObservation(id = bleObservationRoomEntity.id,
                timestampUTCMillis =  bleObservationRoomEntity.timestampUTCMillis,
                location = LocationRoomEntity.toLocation(bleObservationRoomEntity.locationRoomEntity),
                observationType = ObservationType.BLE,
                rssi = bleObservationRoomEntity.rssi,
                mac = bleObservationRoomEntity.mac,
                advertisingSid = bleObservationRoomEntity.advertisingSid,
                dataStatus = bleObservationRoomEntity.dataStatus,
                advertisingInterval = bleObservationRoomEntity.advertisingInterval,
                primaryPhy = bleObservationRoomEntity.primaryPhy,
                secondaryPhy = bleObservationRoomEntity.secondaryPhy,
                txPower = bleObservationRoomEntity.txPower,
                connectable = bleObservationRoomEntity.connectable,
                advertiseFlags = bleObservationRoomEntity.advertiseFlags) //, bleObservationEntity.adverisingMap, bleObservationEntity.rawAdvertisement, bleObservationEntity.manufacturerData, bleObservationEntity.serviceData)
        }

        fun fromBLEObservation(bleObservation: BLEObservation): BLEObservationRoomEntity {
            val locationRoomEntity = LocationRoomEntity.fromLocation(bleObservation.location)
            return BLEObservationRoomEntity(bleObservation.id, bleObservation.timestampUTCMillis, locationRoomEntity, bleObservation.rssi, bleObservation.mac, bleObservation.advertisingSid, bleObservation.dataStatus, bleObservation.advertisingInterval, bleObservation.primaryPhy, bleObservation.secondaryPhy, bleObservation.txPower, bleObservation.connectable, bleObservation.advertiseFlags) //, bleObservation.adverisingMap, bleObservation.rawAdvertisement, bleObservation.manufacturerData, bleObservation.serviceData
        }
    }

    override fun toString(): String {
        return "BLEObservationEntity(id=$id, timestampUTCMillis=$timestampUTCMillis, locationEntity=$locationRoomEntity, observationType=$observationType, rssi=$rssi, mac=$mac, advertisingSid=$advertisingSid, dataStatus=$dataStatus, advertisingInterval=$advertisingInterval, primaryPhy=$primaryPhy, secondaryPhy=$secondaryPhy, txPower=$txPower, connectable=$connectable, advertiseFlags=$advertiseFlags)"
    }
}