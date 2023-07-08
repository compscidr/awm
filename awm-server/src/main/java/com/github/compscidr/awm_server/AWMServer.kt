package com.github.compscidr.awm_server

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
            logger.debug("got data")
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