package com.github.compscidr.awm.exporter

import com.jasonernst.awm_common.db.BLEObservation
import com.jasonernst.awm_common.db.Observation
import com.github.compscidr.awm.db.ObservationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.slf4j.LoggerFactory
import java.net.DatagramSocket
import java.net.InetAddress

class UDPExporter<T> {
    val logger = LoggerFactory.getLogger(javaClass)
    var running = false

    fun start(serverAddress: String, serverPort: Int = 5050, observationRepository: ObservationRepository) {
        if (running) {
            logger.warn("Already running, not starting again")
            return
        }
        running = true
        CoroutineScope(Dispatchers.Main).launch {
            observationRepository.getOldestObservation().observeForever { observation: Observation? ->
                logger.debug("Got observation: $observation")
                if (observation != null) {
                    logger.debug("Sending observation: $observation")
                    if (observation is BLEObservation) {
                        logger.debug("Sending BLE observation, num: ${observationRepository.getNumObservations().value}")
                    } else {
                        logger.warn("Unknown observation type")
                        return@observeForever
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val datagramSocket = DatagramSocket()
                            val message = Json.encodeToString(Observation.serializer(), observation).toByteArray()
                            val packet = java.net.DatagramPacket(
                                message,
                                message.size,
                                InetAddress.getByName(serverAddress),
                                serverPort
                            )
                            datagramSocket.send(packet)
                            logger.debug("Sent ${packet.length} bytes to $serverAddress:$serverPort")
                            observationRepository.delete(observation)
                        } catch (e: Exception) {
                            logger.warn("Failed sending data: ", e)
                            Thread.sleep(10000)
                        }
                    }
                } else {
                    logger.debug("No observations to send")
                    Thread.sleep(1000)
                }
            }
        }
    }

    fun stop() {
        running = false
    }
}