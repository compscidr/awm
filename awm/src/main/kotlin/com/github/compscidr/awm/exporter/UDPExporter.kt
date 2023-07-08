package com.github.compscidr.awm.exporter

import com.github.compscidr.awm.db.BLEObservation
import com.github.compscidr.awm.db.BLEObservationRepository
import com.github.compscidr.awm.db.ObservationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.net.DatagramSocket
import java.net.InetAddress

class UDPExporter<T> {
    val logger = LoggerFactory.getLogger(javaClass)
    var running = false

    fun start(serverAddress: String, serverPort: Int = 5050, observationRepository: BLEObservationRepository) {
        if (running) {
            logger.warn("Already running, not starting again")
            return
        }
        running = true
        CoroutineScope(Dispatchers.IO).launch {
            val datagramSocket = DatagramSocket()
            while (running) {
                val observationEntity = observationRepository.getOldest()
                if (observationEntity != null) {
                    val observation = ObservationEntity.toObservation(observationEntity)
                    val btObservation = observation as BLEObservation
                    val message = Json.encodeToString(btObservation).toByteArray()
                    val packet = java.net.DatagramPacket(message, message.size, InetAddress.getByName(serverAddress), serverPort)
                    try {
                        datagramSocket.send(packet)
                        observationRepository.delete(observationEntity)
                        logger.debug("Sent ${packet.length} bytes to $serverAddress:$serverPort")
                    } catch (e: Exception) {
                        logger.warn("Failed sending data: ", e)
                        Thread.sleep(10000)
                    }
                }
            }
        }
    }

    fun stop() {
        running = false
    }
}