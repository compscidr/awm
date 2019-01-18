package io.rightmesh.awm;

import android.util.Log;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;
import com.anadeainc.rxbus.Subscribe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * This class is used to send the stats to the remote server (or if it is unavailable because the
 * device is offline, caching the stats locally
 */
public class StatsLogger {

    private static final String TAG = StatsLogger.class.getCanonicalName();

    Bus eventBus = BusProvider.getInstance();
    GPSStats lastPosition = new GPSStats(0,0);
    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;

    public void start() {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("data.dat", true));
            bufferedReader = new BufferedReader(new FileReader("data.dat"));

        } catch(IOException ex) {
            ex.printStackTrace();
        }
        eventBus.register(this);
    }

    public void stop() {
        eventBus.unregister(this);

        try {
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
            if(bufferedReader != null) {
                bufferedReader.close();
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    @Subscribe
    public void updateBTDevices(BluetoothStats btStats) {
        btStats.setPosition(lastPosition);
        logNetwork(btStats);
    }

    @Subscribe
    public void updateWiFiStats(WiFiStats wiFiStats) {
        wiFiStats.setPosition(lastPosition);
        logNetwork(wiFiStats);
    }

    @Subscribe
    public void updateGPS(GPSStats gpsStats) {
        lastPosition = gpsStats;
    }

    //used to log to disk in case the network fails
    private void logDisk(NetworkStat networkStat) {

    }

    //todo: check if there is an internet connection first
    private void logNetwork(NetworkStat networkStat) {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

        Runnable sendData = () -> {
            try {
                URL url = new URL("https://test.rightmesh.io/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String jsondata = "{\n" +
                        "  \"stats\": {\n";

                if(networkStat instanceof BluetoothStats) {
                    jsondata = jsondata + "  \t\"bt\": [\n";
                } else if (networkStat instanceof WiFiStats) {
                    jsondata = jsondata + "  \t\"wifi\": [\n";
                } else {
                    Log.d(TAG, "Unknown stats instance. ignoring");
                    return;
                }

                jsondata = jsondata + "  \t\t[" + networkStat.getPosition().longitude + " , " + networkStat.getPosition().latitude + ", " + networkStat.getSize() + "]\n" +
                        "  \t]\n" +
                        "  }\n" +
                        "}\n";

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsondata);
                os.flush();
                os.close();
                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());
                conn.disconnect();
            } catch(Exception ex) {
                //ex.printStackTrace();
            }
        };
        new Thread(sendData).start();
    }
}
