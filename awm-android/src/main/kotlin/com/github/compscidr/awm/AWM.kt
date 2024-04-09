package com.github.compscidr.awm

import android.content.Context
import com.github.compscidr.awm.collector.BLECollector
import com.github.compscidr.awm.db.room.BLEObservationEntity
import com.github.compscidr.awm.db.room.RoomObservationRepository
import com.github.compscidr.awm.db.room.RoomObservationDatabase
import com.github.compscidr.awm.exporter.UDPExporter
import com.github.compscidr.awm.id.ID.getUUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * The main class for the AWM. Starts up the collectors, loggers, and starts emitting events.
 */
class AWM private constructor() {
    lateinit var uuid: UUID
    lateinit var observationRepository: RoomObservationRepository
    var started = false

    companion object {
        private var instance: AWM? = null

        fun getInstance(context: Context): AWM {
            if (instance == null) {
                instance = AWM()
                instance!!.uuid = getUUID(context)
                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    val roomObservationDatabase = RoomObservationDatabase.getInstance(context)
                    instance!!.observationRepository = RoomObservationRepository(roomObservationDatabase.daoMap())
                }
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
        // even if already started
        // todo: iterate though a bunch of collectors and start them all here
        // todo: handle bt, and other radios going down and coming back up
        BLECollector.start(context, observationRepository)

        val btUdpExporter = UDPExporter<BLEObservationEntity>()
        btUdpExporter.start("10.0.0.89", 5050, observationRepository)
    }

    fun stop(context: Context) {
        started = false
        BLECollector.stop(context)
    }
}