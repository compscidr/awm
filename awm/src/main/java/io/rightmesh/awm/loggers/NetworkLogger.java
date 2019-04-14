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

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import io.rightmesh.awm.ObservingDevice;
import io.rightmesh.awm.stats.NetworkDevice;
import io.rightmesh.awm.stats.NetworkStat;

public class NetworkLogger implements StatsLogger {

    private static final String TAG = NetworkLogger.class.getCanonicalName();
    private boolean privacy;
    private String urlString;
    private Bus eventBus;
    private Context context;

    public NetworkLogger(Context context, boolean privacy, String url) {
         eventBus = BusProvider.getInstance();
         this.context = context;
         this.privacy = privacy;
         this.urlString = url;
    }

    /**
     * Tries to upload
     * @param jsonData the json encoded entry ready to rock
     * @throws IOException is something went shitty.
     */
    public void uploadJsonEntry(String jsonData) throws IOException {
        //if the length is zero say we uploaded it
        if(jsonData == null || jsonData.length() == 0) {
            Log.d(TAG, "Zero sized entry");
            throw new InvalidParameterException("Zero sized db entry");
        }

        URL url = new URL(urlString);
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
        int status = conn.getResponseCode();
        Log.i("UPLOAD PENDING STATUS", String.valueOf(status));
        Log.i("UPLOAD PENDING MSG", conn.getResponseMessage());
        conn.disconnect();

        if (status != 200) {
           throw new InvalidParameterException(conn.getResponseMessage());
        }

        eventBus.post(new LogEvent(LogEvent.EventType.SUCCESS, LogEvent.LogType.NETWORK, 1));
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

    /**
     * Should only be used when direct network logging is performed. When the dB wishes to upload
     * it should use the uploadJsonEntry function
     * @param stat the NetworkStat to log
     * @param thisDevice the state of the observing device.
     */
    @Override
    public void log(NetworkStat stat, ObservingDevice thisDevice) {
        try {
            uploadJsonEntry(stat.toJSON(thisDevice));
        } catch (JSONException ex) {
            //ignore this entry if this gets thrown
        } catch(IOException ex) {
            //we don't keep track of fails atm so just do nothing
        }
    }
}
