package com.github.compscidr.awm_example

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.compscidr.awm.AWM
import com.github.compscidr.awm.collector.BLECollector
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

// annoying accompanyist bug for location: https://github.com/google/accompanist/issues/819#issuecomment-1059477255
val locationPermissions: List<String> = listOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
)
@OptIn(ExperimentalPermissionsApi::class)
@Stable
interface LocationPermissionsState : MultiplePermissionsState {
    /**
     * Supplies a well-defined measure of time/progression
     */
    val requestCount: UInt
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberLocationPermissionsState(): LocationPermissionsState {
    val requestCountState: MutableState<UInt> = remember { mutableStateOf(0u) }

    val multiplePermissionsState: MultiplePermissionsState =
        rememberMultiplePermissionsState(locationPermissions) {
            if (it.isEmpty()) {
                // BUG in accompanist-permissions library upon configuration change
                return@rememberMultiplePermissionsState
            }

            requestCountState.value++
        }

    return object : LocationPermissionsState, MultiplePermissionsState by multiplePermissionsState {
        override val requestCount: UInt by requestCountState
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val locationPermissionState = rememberLocationPermissionsState()
    val results = BLECollector.scanMap.values.sortedBy { it.firstSeen }
    val awm = AWM.getInstance(context)
    val unsyncedObservations = awm.observationRepository.getNumObservations().observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "AWM Example",
            fontSize = 24.sp
        )
        val bluetoothPermissionsState = rememberMultiplePermissionsState(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                )
            } else {
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH
                )
            }
        )
        if (locationPermissionState.allPermissionsGranted.not()) {
            Text("Need Location Permissions")
            Button(onClick = { locationPermissionState.launchMultiplePermissionRequest() }) {
                Text("Request Location permission")
            }
        }

        if (bluetoothPermissionsState.allPermissionsGranted.not()) {
            Text("Need BT Permissions")
            Button(onClick = { bluetoothPermissionsState.launchMultiplePermissionRequest() }) {
                Text("Request BT permission")
            }
        } else {
            awm.start(context)
            Text("UUID: ${awm.uuid}")
            Text("Unsynced observations: ${unsyncedObservations.value ?: 0}")
            Text("BLE devices: ${results.size}")

            LazyColumn {
                items(results) { entry ->
                    Row {
                        Text(entry.result.toString(),
                            fontSize = 10.sp)
                    }
                }
            }
        }
    }
}