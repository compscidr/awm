package com.jasonernst.awm;

import com.jasonernst.awm.loggers.NetworkLogger;
import com.jasonernst.awm.stats.NetworkStat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AndroidWirelessStatsCollectorTest {

    private static AndroidWirelessStatsCollector androidWirelessStatsCollector;

    @BeforeAll
    public static void init() {
        androidWirelessStatsCollector = new AndroidWirelessStatsCollector();
        ObservingDevice observingDevice = Mockito.mock(ObservingDevice.class);
        androidWirelessStatsCollector.setThisDevice(observingDevice);
    }

    @Test public void updateNetworkStatsTest() {
        NetworkStat networkStat = Mockito.mock(NetworkStat.class);
        NetworkLogger networkLogger = Mockito.mock(NetworkLogger.class);
        androidWirelessStatsCollector.setNetworkLogger(networkLogger);
        androidWirelessStatsCollector.updateNetworkStats(networkStat);
    }
}
