package io.rightmesh.awm.stats;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class NetworkStat {

    public enum DeviceType {
        BLUETOOTH,
        WIFI,
        WIFI_DIRECT
    }

    @Getter @Setter
    private DeviceType type;

    @Setter @Getter
    private Set<NetworkDevice> devices;

    /**
     * Construct a new network observation.
     * @param type the type of device (Bluetooth, Wi-Fi, Wi-Fi direct).
     * @param devices the list of devices observed
     */
    public NetworkStat(DeviceType type, Set<NetworkDevice> devices) {
        this.type = type;
        this.devices = devices;
    }
}
