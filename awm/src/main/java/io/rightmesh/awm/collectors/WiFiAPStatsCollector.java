package io.rightmesh.awm.collectors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.rightmesh.awm.stats.NetworkDevice;
import io.rightmesh.awm.stats.NetworkStat;
import lombok.Getter;

public class WiFiAPStatsCollector extends StatsCollector {

    private static final String TAG = WiFiAPStatsCollector.class.getCanonicalName();

    private Context context;
    private WifiManager wifiManager;
    private WifiManager.WifiLock wifiLock;
    private WiFiScanReceiver wiFiScanReceiver;

    @Getter
    private String myAddress;

    public WiFiAPStatsCollector(Context context) {
        this.context = context;

        wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "AWM-LIB");
        wiFiScanReceiver = new WiFiScanReceiver();

    }

    @Override
    public void start() throws Exception {
        if(wifiLock != null) {
            wifiLock.acquire();
        }
        context.registerReceiver(wiFiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        myAddress = wifiManager.getConnectionInfo().getMacAddress();
        wifiManager.startScan();
    }

    @Override
    public void stop() {
        if(wifiLock != null) {
            wifiLock.release();
        }
        context.unregisterReceiver(wiFiScanReceiver);
    }

    public class WiFiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //todo on android 8 and 9 we should check if these scans are new or not

            List<ScanResult> scans = wifiManager.getScanResults();
            Set<NetworkDevice> devices = new HashSet<>();
            if(scans.size() == 0) {
                Log.d(TAG, "Scan empty. Waiting for another OS scan or an app resume");
                //wifiManager.startScan();
            } else {
                Log.d(TAG, "GOT SCAN");
                for(ScanResult scan : scans) {
                    Log.d(TAG, scan.BSSID + " " + scan.SSID + "\n  " + scan);
                    NetworkDevice networkDevice = new NetworkDevice(
                            scan.SSID,
                            scan.BSSID,
                            scan.level,
                            scan.frequency,
                            scan.channelWidth,
                            scan.capabilities);
                    devices.add(networkDevice);
                }
                eventBus.post(new NetworkStat(NetworkStat.DeviceType.WIFI, devices));
            }
        }
    }
}
