package com.jasonernst.awm.collectors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;
import com.anadeainc.rxbus.Subscribe;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.jasonernst.awm.loggers.WiFiScan;
import com.jasonernst.awm.stats.ObservedDevice;
import com.jasonernst.awm.stats.NetworkStat;

public class WiFiAPStatsCollector extends StatsCollector {

    private static final String TAG = WiFiAPStatsCollector.class.getCanonicalName();

    private Context context;
    private WifiManager wifiManager;
    private WifiManager.WifiLock wifiLock;
    private WiFiScanReceiver wiFiScanReceiver;
    private ConcurrentHashMap<String, ObservedDevice> devices;
    private Bus eventBus = BusProvider.getInstance();
    private volatile boolean started = false;

    public WiFiAPStatsCollector(Context context) {
        this.context = context;

        wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "AWM-LIB");
        wiFiScanReceiver = new WiFiScanReceiver();

        devices = new ConcurrentHashMap<>();
    }

    @Override
    public void start() {
        eventBus.register(this);
        if(wifiLock != null) {
            wifiLock.acquire();
        }
        context.registerReceiver(wiFiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();
        started = true;
    }

    @Override
    public void stop() {
        if(started) {
            context.unregisterReceiver(wiFiScanReceiver);
            eventBus.unregister(this);
            if(wifiLock != null) {
                wifiLock.release();
            }
        }
        started = false;
    }

    @Subscribe public void startScan(WiFiScan wiFiScan) {
        devices.clear();
        wifiManager.startScan();
    }

    public class WiFiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //todo on android 8 and 9 we should check if these scans are new or not

            List<ScanResult> scans = wifiManager.getScanResults();
            if(scans.size() == 0) {
                wifiManager.startScan();
            } else {
                Log.d(TAG, "GOT SCAN");
                for (ScanResult scan : scans) {
                    Log.d(TAG, scan.BSSID + " " + scan.SSID + "\n  " + scan);


                    ObservedDevice observedDevice = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        observedDevice = new ObservedDevice(
                                scan.SSID,
                                scan.BSSID,
                                scan.level,
                                scan.frequency,
                                scan.channelWidth,
                                scan.capabilities,
                                ObservedDevice.DeviceType.WIFI);
                    } else {
                        observedDevice = new ObservedDevice(
                                scan.SSID,
                                scan.BSSID,
                                scan.level,
                                scan.frequency,
                                0,
                                scan.capabilities,
                                ObservedDevice.DeviceType.WIFI);
                    }
                    devices.put(observedDevice.getMac(), observedDevice);
                }
                Log.d(TAG, "POSTING WIFI EVENT on thread: " + Thread.currentThread().getName());
                eventBus.post(new NetworkStat(thisDevice, devices));
            }
        }
    }
}
