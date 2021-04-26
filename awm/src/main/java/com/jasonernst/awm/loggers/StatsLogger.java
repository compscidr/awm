package com.jasonernst.awm.loggers;

import com.jasonernst.awm.AwmComponent;
import com.jasonernst.awm.ObservingDevice;
import com.jasonernst.awm.stats.NetworkStat;

public interface StatsLogger extends AwmComponent {
    void log(NetworkStat stat, ObservingDevice thisDevice) throws Exception;
}
