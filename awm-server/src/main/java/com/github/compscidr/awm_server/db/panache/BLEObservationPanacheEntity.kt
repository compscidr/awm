package com.github.compscidr.awm_server.db.panache

import com.jasonernst.awm_common.db.Observation
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Entity

@Entity
class BLEObservationPanacheEntity: ObservationPanacheEntity() {
}