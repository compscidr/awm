package com.github.compscidr.awm_server.db.panache

import jakarta.persistence.Embeddable

@Embeddable
class LocationPanacheEntity(
    val timestampUTC: Long = 0L, // should be the same value as Observation.timestampUTC
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val altitude: Double = 0.0,
    val accuracy: Float = 0.0f,
    val speed: Float = 0.0f,
    val bearing: Float = 0.0f,
    val provider: String = "",
)