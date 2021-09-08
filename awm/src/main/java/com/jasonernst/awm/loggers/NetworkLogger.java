package com.jasonernst.awm.loggers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import androidx.core.util.Pair;     // https://stackoverflow.com/a/59462618/5166430

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;

import org.json.JSONException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

import com.jasonernst.awm.ObservingDevice;
import com.jasonernst.awm.stats.NetworkStat;

public class NetworkLogger implements StatsLogger {

    private static final String TAG = NetworkLogger.class.getCanonicalName();
    private boolean privacy;
    private boolean wifiUploads;
    private String urlString;
    private Bus eventBus;
    private Context context;

    public NetworkLogger(Context context, boolean privacy, boolean wifiUploads, String url) {
         eventBus = BusProvider.getInstance();
         this.context = context;
         this.privacy = privacy;
         this.wifiUploads = wifiUploads;
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

    public boolean isMobileConnected() {
        Pair<Boolean, Boolean> isConnected = isWifiIsMobileConnected();
        return isConnected.second;
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
            } else {
                isWifiConn = false;
                isMobileConn = false;
            }
        }

        return Pair.create(isWifiConn, isMobileConn);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo == null) {
            Log.d(TAG, "NETWORK INFO NULL, Can't determine connectivity");
        } else {
            Log.d(TAG, "CONNECTED? " + networkInfo.isConnected());
        }

        if (wifiUploads && !isWifiConnected()) {
            Log.d(TAG, "Wi-Fi uploads only and NOT on Wi-Fi");
            return false;
        }

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
