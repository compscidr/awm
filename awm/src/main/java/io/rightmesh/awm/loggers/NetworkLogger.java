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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

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
     * Tries to upload
     * @param jsonData the json encoded entry ready to rock
     * @throws IOException is something went shitty.
     */
    public void uploadLoad(String jsonData) throws IOException {
        //if the length is zero say we uploaded it
        if(jsonData.length() == 0) {
            Log.d(TAG, "Zero sized entry");
            throw new IOException("Zero sized db entry");
        }

        URL url = new URL(DBURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
        os.writeBytes(jsonData);
        os.flush();
        os.close();
        Log.i("UPLOAD PENDING DATA", jsonData);
        Log.i("UPLOAD PENDING STATUS", String.valueOf(conn.getResponseCode()));
        Log.i("UPLOAD PENDING MSG", conn.getResponseMessage());
        conn.disconnect();

        //eventBus.post(new LogEvent(LogEvent.EventType.SUCCESS, LogEvent.LogType.NETWORK, 1));
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

    @Override
    public void log(NetworkStat stat, ObservingDevice thisDevice) {
        //do nothing because its handled by the db logger now.
    }
}
