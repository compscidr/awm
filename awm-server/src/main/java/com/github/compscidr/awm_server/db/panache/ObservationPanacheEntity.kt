package com.github.compscidr.awm_server.db.panache

import com.jasonernst.awm_common.db.ObservationType
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*

// https://medium.com/jpa-java-persistence-api-guide/mastering-embedded-entities-in-hibernate-and-spring-data-jpa-a-comprehensive-guide-88a6cab58675
// https://www.baeldung.com/hibernate-inheritance
@MappedSuperclass
open class ObservationPanacheEntity(
    open val timestampUTCMillis: Long = 0L,
    @Enumerated(EnumType.STRING) // https://thorben-janssen.com/hibernate-enum-mappings/ less efficient but less prone to breaking
    open val observationType: ObservationType = ObservationType.UNKNOWN,
    open val rssi: Int = 0,
    open val mac: String = "", // bssid on wifi, address on ble
    // since we always expect a location entry to be attached to a observation, we can just use an embedded type
    // later on if we find this to be too inaccurate we can make periodic location entries separately from observations
    // and then use some interopolation along with speed + direction of previous measurements to determine a more
    // precise location of the observation. For now, let's just keep it simple. (On the android side, we get at
    // timestamp for when the location was current, and it may not co-i
    @Embedded open var locationRoomEntity: LocationPanacheEntity = LocationPanacheEntity(),
): PanacheEntity()