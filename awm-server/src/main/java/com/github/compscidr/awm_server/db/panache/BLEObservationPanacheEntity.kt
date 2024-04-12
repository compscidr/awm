package com.github.compscidr.awm_server.db.panache

import com.jasonernst.awm_common.db.Observation
import com.jasonernst.awm_common.db.ObservationType
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Entity

@Entity
class BLEObservationPanacheEntity(
    timestampUTCMillis: Long = 0L,
    rssi: Int = 0,
    mac: String = "",
    locationPanacheEntity: LocationPanacheEntity = LocationPanacheEntity(),
    val advertisingSid: Int = 0,
    val dataStatus: Int = 0,
    val advertisingInterval: Int = 0,
    val primaryPhy: Int = 0,
    val secondaryPhy: Int = 0,
    val txPower: Int = 0,
    val connectable: Boolean = false,
    val advertiseFlags: Int = 0
): ObservationPanacheEntity(timestampUTCMillis, ObservationType.BLE, rssi, mac, locationPanacheEntity) {

}