package io.rightmesh.awm.loggers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;

import java.io.DataOutputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import io.rightmesh.awm.ObservingDevice;
import io.rightmesh.awm.stats.NetworkDevice;
import io.rightmesh.awm.stats.NetworkStat;

public class NetworkLogger implements StatsLogger {

    private static final String TAG = NetworkLogger.class.getCanonicalName();
    private static final String DBURL = "https://test.rightmesh.io/awm-lib-server/";
    private Bus eventBus;
    private Context context;

    public NetworkLogger(Context context) {
         eventBus = BusProvider.getInstance();
         this.context = context;
    }

    /**
     * Logs a single record from the network if internet is available.
     * @param stat
     * @param thisDevice
     * @throws Exception
     */
    @Override
    public void log(NetworkStat stat, ObservingDevice thisDevice) throws Exception {

        if(thisDevice.getPosition().latitude == 0 || thisDevice.getPosition().longitude == 0) {
            Log.d(TAG, "null position. ignoring this measure");
            return;
        }

        //DecimalFormat df = new DecimalFormat("#.####");
        //df.setRoundingMode(RoundingMode.CEILING);

        Log.d(TAG, "LOGGING TO NETWORK!");

        Runnable sendData = () -> {
            try {
                URL url = new URL(DBURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String deviceJson = "";

                int count = 1;
                for(NetworkDevice network : stat.getDevices()) {
                    deviceJson += "{\n";
                    deviceJson += "\t\"mac_address\": \"" + network.getMac() + "\",\n";
                    deviceJson += "\t\"signal_strength\": " + network.getSignalStrength() + ",\n";
                    deviceJson += "\t\"frequency\": " + network.getFrequency() + ",\n";
                    deviceJson += "\t\"channel_width\": " + network.getChannelWidth() + ",\n";
                    deviceJson += "\t\"security\": \"" + network.getSecurity() + "\",\n";
                    if(stat.getType() == NetworkStat.DeviceType.BLUETOOTH) {
                        deviceJson += "\t\"mac_type\": " + 0 + ",\n";
                    } else if (stat.getType() == NetworkStat.DeviceType.WIFI) {
                        deviceJson += "\t\"mac_type\": " + 1 + ",\n";
                    } else {
                        deviceJson += "\t\"mac_type\": " + -1 + ",\n";
                    }
                    deviceJson += "\t\"network_name\": \"" + network.getName() + "\"\n";
                    if(count < stat.getDevices().size()) {
                        deviceJson += "},\n";
                    } else {
                        deviceJson += "}\n";
                    }
                    count++;
                }

                String jsondata = thisDevice.prepareJSON(deviceJson, new Timestamp(new Date().getTime()));
                Log.d(TAG, "JSONDATA: \n" + jsondata);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsondata);
                os.flush();
                os.close();
                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());
                conn.disconnect();

                eventBus.post(new LogEvent(LogEvent.EventType.SUCCESS, LogEvent.LogType.NETWORK, 1));
            } catch(Exception ex) {
                Log.d(TAG, "Error sending to network: " + ex.toString());
                ex.printStackTrace();
            }
        };
        new Thread(sendData).start();
    }

    /**
     * Logs all saved records from disk
     */
    public void uploadPendingLogs(ArrayList<String> jsondata) {
        Runnable sendData = () -> {
            try {
                int count = 0;
                for(String data : jsondata) {
                    if(data.length() == 0) {
                        continue;
                    }
                    URL url = new URL(DBURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(data);
                    os.flush();
                    os.close();
                    Log.i("UPLOAD PENINDING DATA", data);
                    Log.i("UPLOAD PENDING STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("UPLOAD PENDING MSG", conn.getResponseMessage());
                    conn.disconnect();
                    count++;

                    if(conn.getResponseCode() == 400) {
                        eventBus.post(new LogEvent(LogEvent.EventType.MALFORMED, LogEvent.LogType.NETWORK));
                        return;
                    }
                }
                eventBus.post(new LogEvent(LogEvent.EventType.SUCCESS, LogEvent.LogType.NETWORK, jsondata.size()));

            } catch(Exception ex) {
                Log.d(TAG, "Error uploading disk to the server: " + ex.toString());
                eventBus.post(new LogEvent(LogEvent.EventType.FAILURE, LogEvent.LogType.NETWORK));
            }
        };
        new Thread(sendData).start();
    }

    public boolean isWifiConnected() {
        Pair<Boolean, Boolean> isConnected = isWifiIsMobileConnected();
        return isConnected.first;
    }

    public Pair<Boolean, Boolean> isWifiIsMobileConnected() {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiConn = false;
        boolean isMobileConn = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (Network network : connMgr.getAllNetworks()) {
                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn |= networkInfo.isConnected();
                }
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn |= networkInfo.isConnected();
                }
            }
        } else {
            NetworkInfo[] networkInfos = connMgr.getAllNetworkInfo();
            if(networkInfos != null) {
                for (NetworkInfo networkInfo : networkInfos) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        isWifiConn |= networkInfo.isConnected();
                    }
                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        isMobileConn |= networkInfo.isConnected();
                    }
                }
            }
        }

        Log.d(TAG, "Wifi connected: " + isWifiConn);
        Log.d(TAG, "Mobile connected: " + isMobileConn);

        return new Pair<Boolean, Boolean>(isWifiConn, isMobileConn);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() {

    }
}
