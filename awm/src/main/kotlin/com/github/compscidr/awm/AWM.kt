package com.github.compscidr.awm

import android.content.Context
import com.github.compscidr.awm.collector.BLECollector
import com.github.compscidr.awm.db.room.BLEObservationEntity
import com.github.compscidr.awm.db.room.BLEObservationRepository
import com.github.compscidr.awm.db.room.ObservationDatabase
import com.github.compscidr.awm.exporter.UDPExporter
import com.github.compscidr.awm.id.ID.getUUID
import java.util.UUID

/**
 * The main class for the AWM. Starts up the collectors, loggers, and starts emitting events.
 */
class AWM private constructor() {
    lateinit var uuid: UUID
    lateinit var observationRepository: BLEObservationRepository
    var started = false

    companion object {
        private var instance: AWM? = null

        fun getInstance(context: Context): AWM {
            if (instance == null) {
                instance = AWM()
                instance!!.uuid = getUUID(context)
                val observationDatabase = ObservationDatabase.getInstance(context)
                instance!!.observationRepository = BLEObservationRepository(observationDatabase.bleObservationDao())
            }
            return instance!!
        }
    }

    fun start(context: Context) {
        if (started) {
            return
        }
        started = true
        // try to start every time so that if permissions have changed we can restart
        // this means the collector must be able to handle start being called multiple times
        // even if alreday started
        // todo: iterate though a bunch of collectors and start them all here
        // todo: handle bt, and other radios going down and coming back up
        BLECollector.start(context, observationRepository)

        val btUdpExporter = UDPExporter<BLEObservationEntity>()
        btUdpExporter.start("10.0.0.15", 5050, observationRepository)
    }

    fun stop(context: Context) {
        started = false
        BLECollector.stop(context)
    }
}