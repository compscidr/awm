package com.jasonernst.awm.collectors;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.concurrent.ConcurrentHashMap;

import com.jasonernst.awm.stats.ObservedDevice;
import com.jasonernst.awm.stats.NetworkStat;

import lombok.Getter;
import lombok.Setter;

public class BluetoothStatsCollector extends StatsCollector {

    private static final String TAG = BluetoothStatsCollector.class.getCanonicalName();

    private Context context;

    private static BluetoothAdapter mBluetoothAdapter;
    private static volatile BluetoothStates btState;
    @Setter
    @Getter
    private BluetoothBroadcastReceiver bluetoothBroadcastReceiver;
    private ConcurrentHashMap<String, ObservedDevice> btDevices;
    @Setter
    private volatile boolean started = false;

    enum BluetoothStates {
        ON, OFF, REJECTED
    }

    public BluetoothStatsCollector(Context context) {
        this.context = context;

        BluetoothManager bluetoothManager = (BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager == null) {
            Log.d(TAG, "BTMANAGER null");
            return;
        }

        mBluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
        btDevices = new ConcurrentHashMap<>();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(bluetoothBroadcastReceiver, filter);
    }

    @Override
    public void start() throws Exception {
        if (mBluetoothAdapter == null) {
            throw new Exception("Bluetooth adapter is null, likely not supported by phone");
        }

        //turn on BT if its not already on
        if (!mBluetoothAdapter.isEnabled()) {
            btState = BluetoothStates.OFF;
            Log.i(TAG, "Bluetooth off, requesting to turn on");
            context.startActivity(new Intent(context, BluetoothEnableActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            btState = BluetoothStates.ON;
            Log.i(TAG, "Bluetooth already on. Starting discovery");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mBluetoothAdapter.startDiscovery();
        }

        started = true;
    }

    @Override
    public void stop() {
        //we may want to consider restoring the initial state of the bt device from before
        //the library started, ie if it was on, leave it on, if it was off, turn it back off.

        if (bluetoothBroadcastReceiver != null && started) {
            context.unregisterReceiver(bluetoothBroadcastReceiver);
            bluetoothBroadcastReceiver = null;
        }
        started = false;
    }

    public class BluetoothBroadcastReceiver extends BroadcastReceiver {

        private final String TAG = BluetoothBroadcastReceiver.class.getCanonicalName();

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                Log.d(TAG, "Broadcast action is null, can't continue to process the intent");
                return;
            }

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null) {
                    Log.d(TAG, "Device is null in the broadcastreceiver");
                } else {
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Log.d(TAG, "Found: " + device.getName() + " " + device.getAddress() + " RSSI: " + rssi);
                    ObservedDevice observedDevice = new ObservedDevice(
                            device.getName(),
                            device.getAddress(),
                            rssi,
                            0,
                            0,
                            "",
                            ObservedDevice.DeviceType.BLUETOOTH
                    );
                    btDevices.put(observedDevice.getMac(), observedDevice);
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                if (btDevices.size() > 0) {
                    Log.d(TAG, "After scan found a total of " + btDevices.size() + " devices");
                    eventBus.post(new NetworkStat(thisDevice, new ConcurrentHashMap<>(btDevices)));
                    btDevices.clear();
                } else {
                    Log.d(TAG, "Found zero BT devices after scan. starting again.");
                }
                mBluetoothAdapter.startDiscovery();
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Log.d(TAG, "Starting discovery scan");
            }
        }
    }

    /**
     * This class is so that we can capture the result of whether the user agreed to turn on the
     * bluetooth device.
     */
    public static class BluetoothEnableActivity extends Activity {

        private final String TAG = BluetoothEnableActivity.class.getCanonicalName();
        public static final int REQUEST_ENABLE_BT = 109;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Log.d(TAG, "BTENABLEACTIVITY");

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_ENABLE_BT) {
                Log.d(TAG, "REQUEST ENABLE BT RESULT: " + resultCode);
                if (mBluetoothAdapter.isEnabled()) {
                    Log.d(TAG, "Accepted turning on BT device. Starting discovery.");
                    btState = BluetoothStates.ON;
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mBluetoothAdapter.startDiscovery();
                } else {
                    btState = BluetoothStates.REJECTED;
                }
                finish();
            } else {
                Log.d(TAG, "Unknown request code received by " + TAG + " activity");
            }
        }
    }
}
