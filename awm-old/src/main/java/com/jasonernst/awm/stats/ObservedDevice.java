package com.jasonernst.awm.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class is meant to represent an observed Network Device from a Wi-Fi or Bluetooth scan.
 * None of the fields are required, the more that are provided, the more clearer a picture of the
 * state of connectivity in the region is. What can be provided is dependent on the permissions
 * given to the operating system, chipset, etc.
 */
@Data @AllArgsConstructor
public class ObservedDevice {

    public enum DeviceType {
        BLUETOOTH((short)1), WIFI((short)2), WIFI_DIRECT((short)3);

        private final short value;
        private DeviceType(short value) { this.value = value; }
        public short getValue() { return value; }
    }

    private String name;
    private String mac;
    private int signalStrength;
    private int frequency;
    private int channelWidth;
    private String security;
    private DeviceType type;
}
