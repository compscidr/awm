package io.rightmesh.awm.loggers;

import android.content.Context;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import io.rightmesh.awm.stats.GPSStats;
import io.rightmesh.awm.stats.NetworkDevice;
import io.rightmesh.awm.stats.NetworkStat;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(RxJavaTestExtension.class)
public class TestDiskLogger {

    private static final String TAG         = TestDiskLogger.class.getCanonicalName();

    private static final String testUuid    = "8bc1e828-e0b4-4132-8f7f-36dddd279f10";

    @Mock Context context;
    @Mock FileInputStream fileInputStream;
    @Mock FileOutputStream fileOutputStream;

    @BeforeEach
    public void init() throws FileNotFoundException {
        MockitoAnnotations.initMocks(this);

        when(context.openFileOutput(anyString(), anyInt())).thenReturn(fileOutputStream);
        when(context.openFileInput(anyString())).thenReturn(fileInputStream);
    }

    @Test
    @DisplayName("Testing data file reset")
    public void testCleanFileTrue() throws Exception {
        DiskLogger diskLogger = new DiskLogger(context, true, false);
        diskLogger.start();
        Mockito.verify(context, times(1)).deleteFile(anyString());
        Mockito.verify(context, times(1)).openFileInput(anyString());
        Mockito.verify(context, times(1)).openFileOutput(anyString(), anyInt());
        diskLogger.stop();
    }

    @Test
    @DisplayName("Testing normal operation (not cleaning the file)")
    public void testCleanFileFalse() throws Exception {
        DiskLogger diskLogger = new DiskLogger(context, false, false);
        diskLogger.start();
        Mockito.verify(context, times(0)).deleteFile(anyString());
        Mockito.verify(context, times(1)).openFileInput(anyString());
        Mockito.verify(context, times(1)).openFileOutput(anyString(), anyInt());
        diskLogger.stop();
    }

    /**
     * Performs the diskLogger.Log but connects the output and input together so that the DiskLogger
     * works like it's running directly on the phone.
     *
     * @param diskLogger the DiskLogger instance we should log to
     * @param networkStats the NetworkStats to log
     * @param observingDevice the device state at time of logging.
     */
    void logOutputInput(DiskLogger diskLogger, ArrayList<NetworkStat>networkStats,
                        ObservingDevice observingDevice) throws IOException {
        //setup the writer so it isn't writing to the real file
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        diskLogger.setBufferedWriter(writer);

        for (NetworkStat networkStat : networkStats) {
            diskLogger.log(networkStat, observingDevice);
        }

        String output = stream.toString();
        System.out.println("OUTPUT:");
        System.out.println(output);

        //connect the inputstream to the outputstream of the last step
        ByteArrayInputStream istream = new ByteArrayInputStream(output.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
        diskLogger.setBufferedReader(reader);
    }

    @Nested
    @DisplayName("Testing logging of various numbers of devices, and observations")
    public class testLoggingNetworkDevice {

        NetworkDevice networkDevice;
        NetworkDevice networkDevice2;
        ObservingDevice thisDevice;

        @BeforeEach
        public void setup() {
            networkDevice = new NetworkDevice(
                    "testname",
                    "00:11:22:33:44:55",
                    -45,
                    2412,
                    20,
                    "wpa2");

            networkDevice2 = new NetworkDevice(
                    "testname2",
                    "22:33:44:55:66:77",
                    -55,
                    2432,
                    40,
                    "wep");

            thisDevice = new ObservingDevice(
                    UUID.fromString(testUuid),
                    "jupiter-test");

            thisDevice.updatePosition(new GPSStats(-58.4355621,54.1800128));
        }

        @Test
        @DisplayName("Testing that the single device gets logged correctly to the outputstream")
        public void testLogSingleNetworkDevice() throws IOException {

            ArrayList<NetworkStat> networkStats = new ArrayList<>();
            networkStats.add(new NetworkStat(NetworkStat.DeviceType.WIFI,
                    new HashSet<>(Arrays.asList(networkDevice))));

            DiskLogger diskLogger = new DiskLogger(context, true, false);

            logOutputInput(diskLogger, networkStats, thisDevice);

            ArrayList<String> arrayList = diskLogger.getPendingLogs();
            assertEquals(1, arrayList.size());
        }

        @Test
        @DisplayName("Testing that the multiple devices get logged correctly to the outputstream")
        public void testLogMultipleNetworkDevice() throws IOException {

            ArrayList<NetworkStat> networkStats = new ArrayList<>();
            networkStats.add(new NetworkStat(NetworkStat.DeviceType.WIFI,
                    new HashSet<>(Arrays.asList(networkDevice, networkDevice2))));

            DiskLogger diskLogger = new DiskLogger(context, true, false);

            logOutputInput(diskLogger, networkStats, thisDevice);

            ArrayList<String> arrayList = diskLogger.getPendingLogs();
            assertEquals(1, arrayList.size());
        }

        @Test
        @DisplayName("Testing that multiple calls to log result in multiple 'records' in the outputfile")
        public void testLogMultipleLogsNetworkDevice() throws IOException {

            ArrayList<NetworkStat> networkStats = new ArrayList<>();
            NetworkDevice networkDevice3 = new NetworkDevice(networkDevice);
            networkDevice3.setName("testname3");
            NetworkDevice networkDevice4 = new NetworkDevice(networkDevice);
            networkDevice4.setName("testname4");
            NetworkDevice networkDevice5 = new NetworkDevice(networkDevice);
            networkDevice5.setName("testname5");
            NetworkDevice networkDevice6 = new NetworkDevice(networkDevice);
            networkDevice6.setName("testname6");
            NetworkDevice networkDevice7 = new NetworkDevice(networkDevice);
            networkDevice7.setName("testname7");
            NetworkDevice networkDevice8 = new NetworkDevice(networkDevice);
            networkDevice8.setName("testname8");
            NetworkDevice networkDevice9 = new NetworkDevice(networkDevice);
            networkDevice9.setName("testname9");
            NetworkDevice networkDevice10 = new NetworkDevice(networkDevice);
            networkDevice10.setName("testname10");

            networkStats.add(new NetworkStat(NetworkStat.DeviceType.WIFI,
                    new HashSet<>(Arrays.asList(
                            networkDevice, networkDevice3, networkDevice4, networkDevice4,
                            networkDevice5, networkDevice6, networkDevice7, networkDevice8,
                            networkDevice9, networkDevice10))));

            networkStats.add(new NetworkStat(NetworkStat.DeviceType.WIFI,
                    new HashSet<>(Arrays.asList(networkDevice2))));

            DiskLogger diskLogger = new DiskLogger(context, true, false);

            logOutputInput(diskLogger, networkStats, thisDevice);

            ArrayList<String> arrayList = diskLogger.getPendingLogs();
            assertEquals(2, arrayList.size());
        }
    }
}
