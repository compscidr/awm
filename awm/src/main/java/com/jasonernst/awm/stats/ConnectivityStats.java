package com.jasonernst.awm.stats;

import java.net.Inet4Address;
import java.net.Inet6Address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class ConnectivityStats {
    private Inet6Address inet6Address;
    private Inet4Address inet4Address;
    private boolean isWifiConnected;
    private boolean isCellularConnected;
}
