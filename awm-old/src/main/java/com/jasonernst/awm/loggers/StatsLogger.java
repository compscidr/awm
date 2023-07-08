package com.jasonernst.awm.loggers;

import com.jasonernst.awm.AwmComponent;
import com.jasonernst.awm.ReportingDevice;
import com.jasonernst.awm.stats.NetworkStat;

public interface StatsLogger extends AwmComponent {
    void log(NetworkStat stat, ReportingDevice thisDevice) throws Exception;
}
