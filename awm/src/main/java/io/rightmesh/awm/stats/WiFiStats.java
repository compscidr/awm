package io.rightmesh.awm;

import android.net.wifi.ScanResult;

import java.util.Collection;

public class WiFiStats extends NetworkStat {

    WiFiStats(Collection<ScanResult> wifiDevices, String mac) {
        this.mac = mac;
        for(ScanResult scan : wifiDevices) {
            macs.add(scan.BSSID);
        }

    }
}
