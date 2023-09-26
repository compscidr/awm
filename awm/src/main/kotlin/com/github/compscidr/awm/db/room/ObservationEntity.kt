package com.github.compscidr.awm.db.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.compscidr.awm.db.BLEObservation
import com.github.compscidr.awm.db.Observation
import com.github.compscidr.awm.db.ObservationType

// entity inheritance: https://stackoverflow.com/questions/56007170/entity-inheritance-in-android-room
@Entity
open class ObservationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestampUTCMillis: Long,
    @Embedded val locationEntity: LocationEntity,
    val observationType: ObservationType,
    val rssi: Int,
    val mac: String, // bssid on wifi, address on ble
) {
    companion object {
        fun toObservation(observationEntity: ObservationEntity): Observation {
            if (observationEntity.observationType == ObservationType.BLE) {
                return BLEObservationEntity.toBLEObservation(observationEntity as BLEObservationEntity)
            }
            throw RuntimeException("Unknown observation type")
        }

        fun fromObservation(observation: Observation): ObservationEntity {
            if (observation.observationType == ObservationType.BLE) {
                return BLEObservationEntity.fromBLEObservation(observation as BLEObservation)
            }
            throw RuntimeException("Unknown observation type")
        }
    }
}


