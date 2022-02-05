package com.jasonernst.awm;

import android.util.Log;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.UUID;
import com.jasonernst.awm.stats.GPSStats;

import lombok.Data;

@Data
public class ReportingDevice {

    private UUID uuid;
    private GPSStats position;
    private String OS;
    private String wifiMac;
    private String btMac;
    private Timestamp timestamp;
    private Inet4Address inet4Address;
    private Inet6Address inet6Address;
    private float battery_life = 100;
    private boolean hasCellularInternet = false;
    private boolean hasWiFiInternet = false;
    private float cellularThroughput = 0;
    private float wifiThroughput = 0;
    private int cellularPing = 0;
    private int wifiPing = 0;
    private String cellularOperator = "unknown";
    private int cellularNetworktype = 0;

    private static final String TAG = ReportingDevice.class.getCanonicalName();

    public ReportingDevice(UUID uuid, String OS) {
        try {
            inet4Address = (Inet4Address)Inet4Address.getByName("0.0.0.0");
            inet6Address = (Inet6Address)Inet6Address.getByName("::");
        } catch(UnknownHostException ex) {
            Log.d(TAG, "Unknown host: " + ex.toString());
        }
        this.position = new GPSStats(0,0);
        this.uuid = uuid;
        this.OS = OS;
    }
}
