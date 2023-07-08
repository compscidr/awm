package com.github.compscidr.awm.collector

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.mutableStateMapOf
import androidx.core.app.ActivityCompat
import com.github.compscidr.awm.db.room.BLEObservationEntity
import com.github.compscidr.awm.db.room.BLEObservationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

// https://developer.android.com/reference/android/bluetooth/le/ScanResult
object BLECollector {

    private val logger = LoggerFactory.getLogger(javaClass)
    private var scanCallback: ScanCallback? = null
    private var scanner: android.bluetooth.le.BluetoothLeScanner? = null
    private var locationProvider: FusedLocationProviderClient? = null
    private var running = false

    val scanMap = mutableStateMapOf<String, BLEResult>() // mac address -> result

    fun start(context: Context, observationRepository: BLEObservationRepository) {
        if (running) {
            return
        }
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        scanner = bluetoothAdapter.bluetoothLeScanner

        if (scanner == null) {
            logger.error("Cannot start BLE scan because BT power is off")
            return
        }

        if (!haveLocationPermission(context)) {
            logger.error("Cannot start BLE scan because location permission is not granted")
            return
        }

        locationProvider = LocationServices.getFusedLocationProviderClient(context)

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .build()

        scanCallback = object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                locationProvider?.lastLocation?.addOnSuccessListener { location ->
                    val observation = BLEObservationEntity.fromScanResultAndLocation(result, location)
                    CoroutineScope(Dispatchers.IO).launch {
                        observationRepository.insert(observation)
                    }
                    if (scanMap.containsKey(result.device.address)) {
                        scanMap[result.device.address]?.updateResult(result, location)
                    } else {
                        logger.debug("Got BLE SCAN: ${result.device.address}, at location: $location total devices: ${scanMap.size}")
                        logger.info("$result")
                        scanMap[result.device.address] = BLEResult(result, location)
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                logger.error("onBLEScanFailed: $errorCode")
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                logger.debug("onBLEBatchScanResults: $results")
            }
        }

        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                logger.error("Failed to start BLE scan because we don't have permission")
                return
            }
            scanner?.startScan(emptyList(), scanSettings, scanCallback)
            running = true
        } catch (e: Exception) {
            logger.error("Failed to start BLE scan: $e")
        }
    }

    fun stop(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            logger.error("Failed to stop BLE scan because we don't have permission")
            running = false
            return
        }
        if (scanCallback != null) {
            scanner?.stopScan(scanCallback)
        }
        running = false
    }

    private fun haveLocationPermission(context: Context): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }
}

class BLEResult(var result: ScanResult, var location: Location) {
    val firstSeen = System.currentTimeMillis()
    var lastSeen = System.currentTimeMillis()

    fun updateResult(result: ScanResult, location: Location) {
        this.result = result
        this.location = location
        lastSeen = System.currentTimeMillis()
    }
}