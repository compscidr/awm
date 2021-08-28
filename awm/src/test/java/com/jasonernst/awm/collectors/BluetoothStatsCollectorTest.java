package com.jasonernst.awm.collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.net.Inet4Address;

public class BluetoothStatsCollectorTest {
    private static Context context;
    private static BluetoothStatsCollector collector;
    private static BluetoothManager bluetoothManager;
    private static BluetoothAdapter bluetoothAdapter;

    @BeforeAll
    public static void init() {
        context = Mockito.mock(Context.class);
        bluetoothManager = Mockito.mock(BluetoothManager.class);
        doReturn(bluetoothManager).when(context).getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = Mockito.mock(BluetoothAdapter.class);
        doReturn(bluetoothAdapter).when(bluetoothManager).getAdapter();
        collector = Mockito.spy(new BluetoothStatsCollector(context));
    }

    @Test public void btMangerNotFound() {
        Context context1 = Mockito.mock(Context.class);
        BluetoothStatsCollector collector1 = new BluetoothStatsCollector(context1);
    }

    @Test public void nullStartTest() {
        // null mBluetoothAdapter
        Context context1 = Mockito.mock(Context.class);
        BluetoothManager bluetoothManager1 = Mockito.mock(BluetoothManager.class);
        doReturn(bluetoothManager1).when(context1).getSystemService(Context.BLUETOOTH_SERVICE);
        doReturn(null).when(bluetoothManager1).getAdapter();
        BluetoothStatsCollector collector1 = new BluetoothStatsCollector(context1);
        assertThrows(Exception.class, ()->{collector1.start();});
    }

    @Test
    public void startTest() throws Exception {
        collector.start();

        doReturn(true).when(bluetoothAdapter).isEnabled();
        collector.start();
    }

    @Test
    public void stopTest() {
        // null and false
        collector.setBluetoothBroadcastReceiver(null);
        collector.stop();

        // null and true
        collector.setBluetoothBroadcastReceiver(null);
        collector.setStarted(true);
        collector.stop();

        // not null and false
        BluetoothStatsCollector.BluetoothBroadcastReceiver bluetoothBroadcastReceiver = mock(BluetoothStatsCollector.BluetoothBroadcastReceiver.class);
        collector.setStarted(true);
        collector.setBluetoothBroadcastReceiver(bluetoothBroadcastReceiver);
        collector.stop();

        // not null and false
        bluetoothBroadcastReceiver = mock(BluetoothStatsCollector.BluetoothBroadcastReceiver.class);
        collector.setBluetoothBroadcastReceiver(bluetoothBroadcastReceiver);
        collector.setStarted(false);
        collector.stop();
    }

    @Test public void onReceiveTest() {
        // null action
        Intent intent = Mockito.mock(Intent.class);
        collector.getBluetoothBroadcastReceiver().onReceive(context, intent);

        // empty action
        doReturn("").when(intent).getAction();
        collector.getBluetoothBroadcastReceiver().onReceive(context, intent);

        // ACTION DISCOVERY FINISHED (do once before found so we have zero devices
        doReturn(BluetoothAdapter.ACTION_DISCOVERY_FINISHED).when(intent).getAction();
        collector.getBluetoothBroadcastReceiver().onReceive(context, intent);

        // ACTION_FOUND
        doReturn(BluetoothDevice.ACTION_FOUND).when(intent).getAction();
        collector.getBluetoothBroadcastReceiver().onReceive(context, intent);

        BluetoothDevice bluetoothDevice = Mockito.mock(BluetoothDevice.class);
        doReturn(bluetoothDevice).when(intent).getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        doReturn("SOME NAME").when(bluetoothDevice).getName();
        doReturn("SOME ADDR").when(bluetoothDevice).getAddress();
        collector.getBluetoothBroadcastReceiver().onReceive(context, intent);

        // ACTION DISCOVERY FINISHED (do once after action found so we have devices found)
        doReturn(BluetoothAdapter.ACTION_DISCOVERY_FINISHED).when(intent).getAction();
        collector.getBluetoothBroadcastReceiver().onReceive(context, intent);

        doReturn(BluetoothAdapter.ACTION_DISCOVERY_STARTED).when(intent).getAction();
        collector.getBluetoothBroadcastReceiver().onReceive(context, intent);
    }
}
