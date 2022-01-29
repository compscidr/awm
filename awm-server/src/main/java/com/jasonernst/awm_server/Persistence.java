package com.jasonernst.awm_server;

import com.jasonernst.awm_common.stats.NetworkStat;
import com.jasonernst.awm_common.stats.ObservedDevice;

import java.awt.geom.Rectangle2D;
import java.util.List;

public interface Persistence {
    boolean connect();
    List<ObservedDevice> getObservedDevicesByBoundingBox(Rectangle2D rectangle2D);
    List<ObservedDevice> getObservedWifiDevicesByBoundingBox(Rectangle2D rectangle2D);
    List<ObservedDevice> getObservedBluetoothDevicesByBoundingBox(Rectangle2D rectangle2D);
    List<NetworkStat> saveNetworkStats(List<NetworkStat> stats);
    boolean disconnect();
}
