package io.rightmesh.awm;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;
import java.util.Collection;

public class BluetoothStats extends NetworkStat implements Serializable {

    private static final long serialVersionUID = 1L;

    BluetoothStats(Collection<BluetoothDevice> btDevices, String mac) {
        this.mac = mac;
        for(BluetoothDevice device : btDevices) {
            macs.add(device.getAddress());
        }
    }
}
