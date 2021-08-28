package com.jasonernst.awm.collectors;

import android.content.Context;
import android.content.Intent;

import com.jasonernst.awm.collectors.BatteryStatsCollector;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

public class BatteryStatsCollectorTest {
    private static Context context;
    private static BatteryStatsCollector collector;

    @BeforeAll public static void init() {
        context = Mockito.mock(Context.class);
        collector = Mockito.spy(new BatteryStatsCollector(context));
    }

    @Test public void startTest() {
        // already started
        collector.setStarted(true);
        collector.start();
        Mockito.verify(context, Mockito.times(0)).registerReceiver(any(), any());

        collector.setStarted(false);
        collector.start();
        Mockito.verify(context, Mockito.times(1)).registerReceiver(any(), any());
    }

    @Test public void stopTest() {
        collector.setStarted(false);
        collector.stop();

        collector.setStarted(true);
        collector.stop();

        collector.setPowerConnectionReceiver(null);
        collector.stop();
    }

    @Test public void onReceiveTest() {
        Intent intent = Mockito.mock(Intent.class);
        collector.getPowerConnectionReceiver().onReceive(context, intent);
    }
}
