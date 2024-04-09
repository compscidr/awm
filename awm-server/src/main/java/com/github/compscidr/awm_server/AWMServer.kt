package com.github.compscidr.awm_server

import com.jasonernst.awm_common.db.BLEObservation
import com.jasonernst.awm_common.db.Observation
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.SocketException
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

object AWMServer {
    private val logger = LoggerFactory.getLogger(javaClass)
    const val PORT = 5050
    const val MAX_PACKET_SIZE = 1024

    @JvmStatic
    fun main(args: Array<String>) {
        val datagramChannel = DatagramChannel.open()
        datagramChannel.bind(InetSocketAddress(PORT))
        datagramChannel.socket().setOption(StandardSocketOptions.SO_REUSEADDR, true)

        logAllIPAddresses()

        val buffer = ByteBuffer.allocate(MAX_PACKET_SIZE)
        logger.debug("Waiting for data")
        while (true) {
            datagramChannel.receive(buffer)
            logger.debug("Got ${buffer.limit()} bytes")
            buffer.flip()
            val bufferBytes = ByteArray(buffer.limit())
            buffer.get(bufferBytes)
            val observationString = String(bufferBytes)
            //logger.debug("Got observation string: $observationString")
            val observation = Json.decodeFromString<Observation>(observationString)
            logger.debug("Got observation: $observation")
        }
    }

    fun logAllIPAddresses() {
        try {
            for (networkInterface in java.net.NetworkInterface.getNetworkInterfaces()) {
                logger.debug("Network Interface: ${networkInterface.name}")
                for (inetAddress in networkInterface.inetAddresses) {
                    logger.debug("  IP Address: ${inetAddress.hostAddress}")
                }
            }
        } catch (e: SocketException) {
            logger.error("Error getting network interfaces", e)
        }
    }
}