package com.github.compscidr.awm_server.db

import com.github.compscidr.awm_server.db.panache.BLEObservationPanacheEntity
import com.github.compscidr.awm_server.db.panache.LocationPanacheEntity
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import org.flywaydb.core.Flyway

@Singleton
class MigrationService {
    @Inject
    private lateinit var flyway: Flyway

    private var migrated = false

    fun onStart(@Observes ev: StartupEvent) {
        checkMigration()
        testAdd()
    }

    @Transactional
    fun testAdd() {
        if (migrated) {
            val bleentity = BLEObservationPanacheEntity(System.currentTimeMillis(), 1, "11:22:33:44:55:66", LocationPanacheEntity(System.currentTimeMillis(), 1.0, 2.0, 3.0, 4.0f, 0.1f, 0.2f, "test"), 1, 2, 3, 4, 5, 6, true, 8)
            bleentity.persist()
        }
    }

    fun checkMigration() {
        if (!migrated) {
            flyway.clean()
            flyway.migrate()
            println("MIGRATION STATUS: ${flyway.info().current().version}")
            migrated = true
        }
    }
}