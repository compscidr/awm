package io.rightmesh.awm;

import android.bluetooth.BluetoothDevice;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BluetoothStats {
    public int size;
    public String mac;                                  //the device reporting the stats mac address
    public List<String> macs = new LinkedList<>();      //the macs that were scanned nearby
    public GPSStats position;

    BluetoothStats(Collection<BluetoothDevice> btDevices, String mac) {
        this.size = btDevices.size();
        for(BluetoothDevice device : btDevices) {
            macs.add(device.getAddress());
        }
    }
}
