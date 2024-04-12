package com.github.compscidr.awm_server

import com.jasonernst.awm_common.db.Observation
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.InetSocketAddress
import java.net.SocketException
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.util.concurrent.atomic.AtomicBoolean

@ApplicationScoped
class AWMServer {

    companion object {
        const val PORT = 5050
        const val MAX_PACKET_SIZE = 1024
    }

    private val logger = LoggerFactory.getLogger(javaClass)
    private var serverThread: Thread? = null
    private var datagramChannel: DatagramChannel? = null
    private val running = AtomicBoolean(false)

    private fun serverLoop() {
        val buffer = ByteBuffer.allocate(MAX_PACKET_SIZE)
        if (running.get().not()) {
            running.set(true)
            try {
                datagramChannel = DatagramChannel.open()
                datagramChannel?.bind(InetSocketAddress(PORT))
                datagramChannel?.socket()?.setOption(StandardSocketOptions.SO_REUSEADDR, true)
            } catch (ex: IOException) {
                logger.error("Error opening and binding to datagram socket on port #$PORT", ex)
                running.set(false)
                try {
                    datagramChannel?.close()
                } catch (ex: IOException) {
                    // ignore
                }
                datagramChannel = null
                return
            }
            logger.info("Started datagram server on port $PORT")
            while (running.get()) {
                logger.info("Waiting for data")
                datagramChannel?.receive(buffer)
                logger.info("Got ${buffer.limit()} bytes")
                buffer.flip()
                val bufferBytes = ByteArray(buffer.limit())
                buffer.get(bufferBytes)
                val observationString = String(bufferBytes)
                //logger.debug("Got observation string: $observationString")
                val observation = Json.decodeFromString<Observation>(observationString)
                logger.info("Got observation: $observation")
            }
        } else {
            logger.warn("Trying to run server twice")
        }
    }

    private fun logAllIPAddresses() {
        try {
            for (networkInterface in java.net.NetworkInterface.getNetworkInterfaces()) {
                logger.info("Network Interface: ${networkInterface.name}")
                for (inetAddress in networkInterface.inetAddresses) {
                    logger.info("  IP Address: ${inetAddress.hostAddress}")
                }
            }
        } catch (e: SocketException) {
            logger.error("Error getting network interfaces", e)
        }
    }

    fun onStart(@Observes ev: StartupEvent) {
        logger.error("Starting server")
        logAllIPAddresses()
        serverThread = Thread {
            serverLoop()
        }
        serverThread?.start()
    }

    fun onStop(@Observes ev: ShutdownEvent) {
        logger.error("Stopping server")
        running.set(false)
        datagramChannel?.close()
        datagramChannel = null
        serverThread?.join()
        logger.info("Stopped server")
    }
}