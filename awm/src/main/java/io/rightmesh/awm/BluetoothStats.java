package io.rightmesh.awm;

import android.bluetooth.BluetoothDevice;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BluetoothStats {
    public int size;
    public List<String> macs = new LinkedList<>();

    BluetoothStats(Collection<BluetoothDevice> btDevices) {
        this.size = btDevices.size();
        for(BluetoothDevice device : btDevices) {
            macs.add(device.getAddress());
        }
    }
}
