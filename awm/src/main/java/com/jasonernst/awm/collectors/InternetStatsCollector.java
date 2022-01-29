package com.jasonernst.awm.collectors;

import android.content.Context;

import com.jasonernst.awm_common.stats.ReportingDevice;

public class InternetStatsCollector extends StatsCollector {

    public InternetStatsCollector(Context context, ReportingDevice thisDevice) {
        this.thisDevice = thisDevice;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() {

    }
}
