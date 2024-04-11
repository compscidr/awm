package com.github.compscidr.awm_server.db

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.flywaydb.core.Flyway

@Singleton
class MigrationService {
    @Inject
    private lateinit var flyway: Flyway

    private var migrated = false

    fun onStart(@Observes ev: StartupEvent) {
        checkMigration()
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