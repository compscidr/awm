package io.rightmesh.awm;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;
import com.vanniktech.rxpermission.Permission;

import java.util.Map;

abstract class StatsCollector {

    Bus eventBus = BusProvider.getInstance();
    Map<String, Permission> permissions = null;

    void setPermissions(Map<String, Permission> permissions) {
        this.permissions = permissions;
    }

    abstract void start() throws Exception;
    abstract void stop();
}
