package io.rightmesh.awm.stats;

import android.util.Log;

import org.json.JSONException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import io.rightmesh.awm.ObservingDevice;
import io.rightmesh.awm.loggers.ObservationDatabase;
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

    public String toJSON(ObservingDevice thisDevice) throws JSONException {

        if(thisDevice.getPosition().latitude == 0 || thisDevice.getPosition().longitude == 0) {
            throw new JSONException("null position. ignoring this measure");
        }

        String deviceJson = "";
        int count = 1;
        for(NetworkDevice network : devices) {
            deviceJson += "\t\t\t{\n";
            deviceJson += "\t\t\t\t\"mac_address\": \"" + network.getMac() + "\",\n";
            deviceJson += "\t\t\t\t\"signal_strength\": " + network.getSignalStrength() + ",\n";
            deviceJson += "\t\t\t\t\"frequency\": " + network.getFrequency() + ",\n";
            deviceJson += "\t\t\t\t\"channel_width\": " + network.getChannelWidth() + ",\n";
            deviceJson += "\t\t\t\t\"security\": \"" + network.getSecurity() + "\",\n";
            if (type == NetworkStat.DeviceType.BLUETOOTH) {
                deviceJson += "\t\t\t\t\"mac_type\": " + 0 + ",\n";
            } else if (type == NetworkStat.DeviceType.WIFI) {
                deviceJson += "\t\t\t\t\"mac_type\": " + 1 + ",\n";
            } else {
                deviceJson += "\t\t\t\t\"mac_type\": " + -1 + ",\n";
            }
            deviceJson += "\t\t\t\t\"network_name\": \"" + network.getName() + "\"\n";
            if (count < devices.size()) {
                deviceJson += "\t\t\t},\n";
            } else {
                deviceJson += "\t\t\t}\n";
            }
            count++;
        }

        return thisDevice.prepareJSON(deviceJson, new Timestamp(new Date().getTime()));
    }
}
