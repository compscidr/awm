package com.jasonernst.awm.collectors;

import android.content.Context;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GPSStatsCollectorTest {

    private static Context context;
    private static GPSStatsCollector collector;

    @BeforeAll
    public static void init() {
        context = Mockito.mock(Context.class);
        collector = Mockito.spy(new GPSStatsCollector(context));
    }

    @Test public void startTest() {
    }
}
