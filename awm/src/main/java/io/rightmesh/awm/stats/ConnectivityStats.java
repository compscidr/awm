package io.rightmesh.awm.stats;

import java.net.Inet4Address;
import java.net.Inet6Address;

import lombok.Getter;
import lombok.Setter;

public class ConnectivityStats {

    @Getter @Setter
    private Inet6Address inet6Address;

    @Getter @Setter
    private Inet4Address inet4Address;

    @Getter @Setter
    private boolean isWifiConnected;

    @Getter @Setter
    private boolean isCellularConnected;

    public ConnectivityStats() {

    }
}
