package com.github.compscidr.awm.db.room

import androidx.room.Entity
import com.jasonernst.awm_common.db.Location

// https://developer.android.com/reference/android/location/Location
@Entity
data class LocationRoomEntity(
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
        fun fromAndroidLocation(androidLocation: android.location.Location): LocationRoomEntity {
            return LocationRoomEntity(
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

        fun fromLocation(location: Location): LocationRoomEntity {
            return LocationRoomEntity(
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

        fun toLocation(locationRoomEntity: LocationRoomEntity): Location {
            return Location(
                timestampUTC = locationRoomEntity.timestampUTC,
                longitude = locationRoomEntity.longitude,
                latitude = locationRoomEntity.latitude,
                altitude = locationRoomEntity.altitude,
                accuracy = locationRoomEntity.accuracy,
                speed = locationRoomEntity.speed,
                bearing = locationRoomEntity.bearing,
                provider = locationRoomEntity.provider,
            )
        }
    }
}