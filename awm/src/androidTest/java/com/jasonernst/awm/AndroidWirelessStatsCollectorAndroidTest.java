package com.jasonernst.awm;

import com.jasonernst.awm_common.stats.ReportingDevice;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AndroidWirelessStatsCollectorAndroidTest {
    private static AndroidWirelessStatsCollector androidWirelessStatsCollector;
    private static ReportingDevice observingDevice;

    @BeforeAll
    public static void init() {
        androidWirelessStatsCollector = Mockito.spy(new AndroidWirelessStatsCollector());
        observingDevice = Mockito.mock(ReportingDevice.class);
        androidWirelessStatsCollector.setThisDevice(observingDevice);
    }

    @Test
    public void startTest() {
        androidWirelessStatsCollector.setFirstLaunch(false);
        androidWirelessStatsCollector.start();
    }
}
