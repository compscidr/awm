package io.rightmesh.awm;

import android.util.Log;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;
import com.anadeainc.rxbus.Subscribe;

import java.io.DataOutputStream;
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

    public void start() {
        eventBus.register(this);
    }

    public void stop() {
        eventBus.unregister(this);
    }

    @Subscribe
    public void updateBTDevices(BluetoothStats btStats) {
        btStats.position = lastPosition;

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
                        "  \"stats\": {\n" +
                        "  \t\"bt\": [\n" +
                        "  \t\t[" + btStats.position.longitude + " , " + btStats.position.latitude + ", " + btStats.size + "]\n" +
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
                ex.printStackTrace();
            }
        };
        new Thread(sendData).start();
    }

    @Subscribe
    public void updateGPS(GPSStats gpsStats) {
        lastPosition = gpsStats;
    }
}
