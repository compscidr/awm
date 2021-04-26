package com.jasonernst.awm.stats;

import lombok.Getter;
import lombok.Setter;

/**
 * This class is meant to represent an observed Network Device from a Wi-Fi or Bluetooth scan.
 * None of the fields are required, the more that are provided, the more clearer a picture of the
 * state of connectivity in the region is. What can be provided is dependent on the permissions
 * given to the operating system, chipset, etc.
 */
public class NetworkDevice {

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String mac;

    @Getter @Setter
    private int signalStrength;

    @Getter @Setter
    private int frequency;

    @Getter @Setter
    private int channelWidth;

    @Getter @Setter
    private String security;

    /**
     * Construt a new Network Device.
     * @param name the SSID of Wi-Fi hotspots, the device name for BT or Wi-Fi Direct
     * @param mac the MAC address of the device
     * @param signalStrength the signal strength in dB between the observer and this device
     * @param frequency the frequency this device is operating on
     * @param channelWidth the width of the channel (Mhz)
     * @param security on Wi-Fi WPA, WEP, Open, etc.
     */
    public NetworkDevice(String name, String mac, int signalStrength,
                         int frequency, int channelWidth, String security) {
        this.name = name;
        this.mac = mac;
        this.signalStrength = signalStrength;
        this.frequency = frequency;
        this.channelWidth = channelWidth;
        this.security = security;
    }


    public NetworkDevice(NetworkDevice other) {
        this.name = other.getName();
        this.mac = other.getMac();
        this.signalStrength = other.getSignalStrength();
        this.frequency = other.getFrequency();
        this.channelWidth = other.getChannelWidth();
        this.security = other.getSecurity();
    }
}
