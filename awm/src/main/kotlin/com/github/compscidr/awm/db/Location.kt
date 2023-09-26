package com.github.compscidr.awm.db

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