package com.jasonernst.awm.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is meant to represent an observed Network Device from a Wi-Fi or Bluetooth scan.
 * None of the fields are required, the more that are provided, the more clearer a picture of the
 * state of connectivity in the region is. What can be provided is dependent on the permissions
 * given to the operating system, chipset, etc.
 */
@Data @AllArgsConstructor
public class NetworkDevice {
    private String name;
    private String mac;
    private int signalStrength;
    private int frequency;
    private int channelWidth;
    private String security;

    public NetworkDevice(NetworkDevice other) {
        this.name = other.getName();
        this.mac = other.getMac();
        this.signalStrength = other.getSignalStrength();
        this.frequency = other.getFrequency();
        this.channelWidth = other.getChannelWidth();
        this.security = other.getSecurity();
    }
}
