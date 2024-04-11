package com.github.compscidr.awm_server.db.panache

import com.jasonernst.awm_common.db.ObservationType
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

// https://medium.com/jpa-java-persistence-api-guide/mastering-embedded-entities-in-hibernate-and-spring-data-jpa-a-comprehensive-guide-88a6cab58675
@Entity
open class ObservationPanacheEntity(
    val timestampUTCMillis: Long = 0L,
    // we could embed the location, which means its all really just one table - however, we probably want to keep a separate list of locations per user
    // because the locations on the phone are periodic. if we want to know the true location, we'll have to interpolate between the last two measurements
    // and maybe take into account the speed. We could even take into account several previous measures to determine the direction of travel as well.
    @Embedded val locationRoomEntity: LocationPanacheEntity = LocationPanacheEntity(),
    @Enumerated(EnumType.STRING) // https://thorben-janssen.com/hibernate-enum-mappings/ less efficient but less prone to breaking
    val observationType: ObservationType = ObservationType.UNKNOWN,
    val rssi: Int = 0,
    val mac: String = "", // bssid on wifi, address on ble
): PanacheEntity() {
}