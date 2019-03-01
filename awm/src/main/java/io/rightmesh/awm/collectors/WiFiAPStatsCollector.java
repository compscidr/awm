package io.rightmesh.awm.collectors;

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
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.rightmesh.awm.loggers.WiFiScan;
import io.rightmesh.awm.stats.NetworkDevice;
import io.rightmesh.awm.stats.NetworkStat;
import lombok.Getter;

public class WiFiAPStatsCollector extends StatsCollector {

    private static final String TAG = WiFiAPStatsCollector.class.getCanonicalName();

    private Context context;
    private WifiManager wifiManager;
    private WifiManager.WifiLock wifiLock;
    private WiFiScanReceiver wiFiScanReceiver;
    private Set<NetworkDevice> devices;
    private Bus eventBus = BusProvider.getInstance();
    private volatile boolean started = false;

    @Getter
    private String myAddress;

    public WiFiAPStatsCollector(Context context) {
        this.context = context;

        wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "AWM-LIB");
        wiFiScanReceiver = new WiFiScanReceiver();

        devices = Sets.newConcurrentHashSet();
    }

    @Override
    public void start() throws Exception {
        eventBus.register(this);
        if(wifiLock != null) {
            wifiLock.acquire();
        }
        context.registerReceiver(wiFiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        myAddress = wifiManager.getConnectionInfo().getMacAddress();
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


                    NetworkDevice networkDevice = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        networkDevice = new NetworkDevice(
                                scan.SSID,
                                scan.BSSID,
                                scan.level,
                                scan.frequency,
                                scan.channelWidth,
                                scan.capabilities);
                    } else {
                        networkDevice = new NetworkDevice(
                                scan.SSID,
                                scan.BSSID,
                                scan.level,
                                scan.frequency,
                                0,
                                scan.capabilities);
                    }
                    devices.add(networkDevice);
                }
                Log.d(TAG, "POSTING WIFI EVENT on thread: " + Thread.currentThread().getName());
                eventBus.post(new NetworkStat(NetworkStat.DeviceType.WIFI, devices));
            }
        }
    }
}
