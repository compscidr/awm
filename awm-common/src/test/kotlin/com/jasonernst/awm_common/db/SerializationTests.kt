package com.jasonernst.awm_common.db

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SerializationTests {
    @Test
    fun bleObservationSerializationTest() {
        val bleObservation = BLEObservation(
            timestampUTCMillis = 1234567890,
            location = Location(System.currentTimeMillis(), 2.0, 3.0, 4.0, 5.0f, 6.0f, 7.0f, "test"),
            rssi = -50,
            mac = "00:11:22:33:44:55",
            advertisingSid = 0,
            dataStatus = 0,
            advertisingInterval = 0,
            primaryPhy = 0,
            secondaryPhy = 0,
            txPower = 0,
            connectable = false,
            advertiseFlags = 0
        )
        val json = Json.encodeToString(Observation.serializer(), bleObservation)
        println(json)
        val deserialized = Json.decodeFromString<Observation>(json)
        assertEquals(bleObservation, deserialized)
    }
}