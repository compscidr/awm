package com.jasonernst.awm.collectors;

import android.content.Context;

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
}
