package com.github.compscidr.awm.db.room

import androidx.room.Entity
import kotlinx.serialization.Serializable

// https://jacquessmuts.github.io/post/modularization_room/
@Serializable
data class Location(
    val timestampUTC: Long,
    val longitude: Double,
    val latitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val speed: Float,
    val bearing: Float,
    val provider: String,
)

// https://developer.android.com/reference/android/location/Location
@Entity
data class LocationEntity(
    val timestampUTC: Long, // should be the same value as Observation.timestampUTC
    val longitude: Double,
    val latitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val speed: Float,
    val bearing: Float,
    val provider: String,
) {
    companion object {
        fun fromAndroidLocation(androidLocation: android.location.Location): LocationEntity {
            return LocationEntity(
                timestampUTC = androidLocation.time,
                longitude = androidLocation.longitude,
                latitude = androidLocation.latitude,
                altitude = androidLocation.altitude,
                accuracy = androidLocation.accuracy,
                speed = androidLocation.speed,
                bearing = androidLocation.bearing,
                provider = androidLocation.provider ?: "",
            )
        }

        fun fromLocation(location: Location): LocationEntity {
            return LocationEntity(
                timestampUTC = location.timestampUTC,
                longitude = location.longitude,
                latitude = location.latitude,
                altitude = location.altitude,
                accuracy = location.accuracy,
                speed = location.speed,
                bearing = location.bearing,
                provider = location.provider,
            )
        }

        fun toLocation(locationEntity: LocationEntity): Location {
            return Location(
                timestampUTC = locationEntity.timestampUTC,
                longitude = locationEntity.longitude,
                latitude = locationEntity.latitude,
                altitude = locationEntity.altitude,
                accuracy = locationEntity.accuracy,
                speed = locationEntity.speed,
                bearing = locationEntity.bearing,
                provider = locationEntity.provider,
            )
        }
    }
}