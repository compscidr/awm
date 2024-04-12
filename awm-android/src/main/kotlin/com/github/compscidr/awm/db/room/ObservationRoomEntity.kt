package com.github.compscidr.awm.db.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jasonernst.awm_common.db.BLEObservation
import com.jasonernst.awm_common.db.Observation
import com.jasonernst.awm_common.db.ObservationType

// entity inheritance: https://stackoverflow.com/questions/56007170/entity-inheritance-in-android-room
@Entity
open class ObservationRoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestampUTCMillis: Long,
    @Embedded val locationRoomEntity: LocationRoomEntity,
    var observationType: ObservationType,
    val rssi: Int,
    val mac: String, // bssid on wifi, address on ble
) {
    companion object {
        fun toObservation(observationRoomEntity: ObservationRoomEntity): Observation {
            if (observationRoomEntity.observationType == ObservationType.BLE) {
                return BLEObservationRoomEntity.toBLEObservation(observationRoomEntity as BLEObservationRoomEntity)
            }
            throw RuntimeException("Unknown observation type")
        }

        fun fromObservation(observation: Observation): ObservationRoomEntity {
            if (observation.observationType == ObservationType.BLE) {
                return BLEObservationRoomEntity.fromBLEObservation(observation as BLEObservation)
            }
            throw RuntimeException("Unknown observation type")
        }
    }
}


