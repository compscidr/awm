package com.github.compscidr.awm.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    }
}


