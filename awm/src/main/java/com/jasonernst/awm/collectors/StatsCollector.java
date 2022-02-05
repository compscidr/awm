package com.jasonernst.awm.collectors;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;
import com.jasonernst.awm.ReportingDevice;
import com.vanniktech.rxpermission.Permission;

import java.util.Map;

import com.jasonernst.awm.AwmComponent;

public abstract class StatsCollector implements AwmComponent {

    ReportingDevice thisDevice = null;
    Bus eventBus = BusProvider.getInstance();
    Map<String, Permission> permissions = null;

    public void setThisDevice(ReportingDevice thisDevice) {
        this.thisDevice = thisDevice;
    }

    public void setPermissions(Map<String, Permission> permissions) {
        this.permissions = permissions;
    }

    public abstract void start() throws Exception;
    public abstract void stop();
}
