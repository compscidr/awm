package com.jasonernst.awm;

import com.anadeainc.rxbus.Bus;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.jasonernst.awm.collectors.StatsCollector;
import com.jasonernst.awm.loggers.DatabaseLogger;
import com.jasonernst.awm.loggers.NetworkLogger;
import com.jasonernst.awm.loggers.StatsLogger;
import com.jasonernst.awm.stats.BatteryStats;
import com.jasonernst.awm.stats.GPSStats;
import com.jasonernst.awm.stats.NetworkStat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

public class AndroidWirelessStatsCollectorTest {

    private static AndroidWirelessStatsCollector androidWirelessStatsCollector;
    private static ReportingDevice reportingDevice;

    @BeforeAll
    public static void init() {
        androidWirelessStatsCollector = Mockito.spy(new AndroidWirelessStatsCollector());
        reportingDevice = Mockito.mock(ReportingDevice.class);
        androidWirelessStatsCollector.setThisDevice(reportingDevice);
    }

    // todo: fix - still needs some work
    @Disabled
    @Test public void constructorTest() {
        Activity activity = mock(Activity.class);
        Context context = mock(Context.class);
        SharedPreferences prefs = mock(SharedPreferences.class);
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        doReturn(context).when(activity).getApplicationContext();
        doReturn(prefs).when(context).getSharedPreferences(anyString(), anyInt());
        doReturn(editor).when(prefs).edit();
        doReturn(context).when(context).getApplicationContext();
        AndroidWirelessStatsCollector awc = new AndroidWirelessStatsCollector(activity,
                false, false, false, false, false, "google.com");
    }

    @Test public void updateNetworkStatsTest() {
        NetworkStat networkStat = Mockito.mock(NetworkStat.class);
        NetworkLogger networkLogger = Mockito.mock(NetworkLogger.class);
        DatabaseLogger databaseLogger = Mockito.mock(DatabaseLogger.class);
        androidWirelessStatsCollector.setNetworkLogger(networkLogger);
        androidWirelessStatsCollector.setDatabaseLogger(databaseLogger);
        // caching = false, wifiUploads = false
        androidWirelessStatsCollector.updateNetworkStats(networkStat);
        verify(androidWirelessStatsCollector, never()).logNetwork(networkStat, reportingDevice);

        // caching = false, wifi uploads = true
        androidWirelessStatsCollector.setWifiUploads(true);
        androidWirelessStatsCollector.updateNetworkStats(networkStat);
        verify(androidWirelessStatsCollector, never()).logNetwork(networkStat, reportingDevice);
        doReturn(true).when(networkLogger).isWifiConnected();
        androidWirelessStatsCollector.updateNetworkStats(networkStat);
        verify(androidWirelessStatsCollector, never()).logNetwork(networkStat, reportingDevice);
        doReturn(true).when(networkLogger).isOnline();
        androidWirelessStatsCollector.updateNetworkStats(networkStat);
        verify(androidWirelessStatsCollector, times(1)).logNetwork(networkStat, reportingDevice);

        // caching = false, wifi uploads = false
        androidWirelessStatsCollector.setWifiUploads(false);
        androidWirelessStatsCollector.updateNetworkStats(networkStat);
        verify(androidWirelessStatsCollector, times(2)).logNetwork(networkStat, reportingDevice);

        // caching = true
        androidWirelessStatsCollector.setCaching(true);
        androidWirelessStatsCollector.updateNetworkStats(networkStat);
        verify(androidWirelessStatsCollector, times(2)).logNetwork(networkStat, reportingDevice);
    }

    @Test public void startLoggers() throws Exception {
        StatsLogger logger = Mockito.mock(StatsLogger.class);
        Set<StatsLogger> statsLoggers = new HashSet<>();
        statsLoggers.add(logger);
        androidWirelessStatsCollector.setStatsLoggers(statsLoggers);
        androidWirelessStatsCollector.startLoggers();
        verify(logger, times(1)).start();

        doThrow(Exception.class).when(logger).start();
        androidWirelessStatsCollector.startLoggers();
    }

    @Test public void startStats() throws Exception {
        StatsCollector collector = Mockito.mock(StatsCollector.class);
        Set<StatsCollector> statsCollectors = new HashSet<>();
        statsCollectors.add(collector);
        androidWirelessStatsCollector.setStatsCollectors(statsCollectors);
        androidWirelessStatsCollector.startStats();
        verify(collector, times(1)).start();

        // exception case
        doThrow(Exception.class).when(collector).start();
        androidWirelessStatsCollector.startStats();
    }

    @Test public void pause() {
        StatsCollector collector = Mockito.mock(StatsCollector.class);
        Set<StatsCollector> statsCollectors = new HashSet<>();
        statsCollectors.add(collector);
        androidWirelessStatsCollector.setStatsCollectors(statsCollectors);
        androidWirelessStatsCollector.pause();
        verify(collector, times(1)).stop();
    }

    @Test public void unpause() throws Exception {
        StatsCollector collector = Mockito.mock(StatsCollector.class);
        Set<StatsCollector> statsCollectors = new HashSet<>();
        statsCollectors.add(collector);
        androidWirelessStatsCollector.setStatsCollectors(statsCollectors);
        androidWirelessStatsCollector.unpause();
        verify(collector, times(1)).start();

        // exception case
        doThrow(Exception.class).when(collector).start();
        androidWirelessStatsCollector.unpause();
    }

    @Test public void stop() {
        // things are null, exception will be thrown
        androidWirelessStatsCollector.stop();

        // good path
        Bus eventBus = Mockito.mock(Bus.class);
        StatsCollector collector = Mockito.mock(StatsCollector.class);
        Set<StatsCollector> statsCollectors = new HashSet<>();
        statsCollectors.add(collector);
        androidWirelessStatsCollector.setStatsCollectors(statsCollectors);
        StatsLogger logger = Mockito.mock(StatsLogger.class);
        Set<StatsLogger> statsLoggers = new HashSet<>();
        statsLoggers.add(logger);
        androidWirelessStatsCollector.setStatsLoggers(statsLoggers);
        androidWirelessStatsCollector.setEventBus(eventBus);
        androidWirelessStatsCollector.stop();
    }

    @Test public void subscriptions() {
        BatteryStats batteryStats = Mockito.mock(BatteryStats.class);
        androidWirelessStatsCollector.updateBattery(batteryStats);

        GPSStats gpsStats = Mockito.mock(GPSStats.class);
        androidWirelessStatsCollector.updateGPS(gpsStats);
    }
}
