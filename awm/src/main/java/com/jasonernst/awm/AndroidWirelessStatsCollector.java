package com.jasonernst.awm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;
import com.anadeainc.rxbus.Subscribe;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
import com.vanniktech.rxpermission.Permission;
import com.vanniktech.rxpermission.RealRxPermission;
import com.vanniktech.rxpermission.RxPermission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import com.jasonernst.awm.collectors.BatteryStatsCollector;
import com.jasonernst.awm.collectors.BluetoothStatsCollector;
// import com.jasonernst.awm.collectors.GPSStatsCollector;
import com.jasonernst.awm.collectors.InternetStatsCollector;
import com.jasonernst.awm.collectors.StatsCollector;
import com.jasonernst.awm.collectors.WiFiAPStatsCollector;
import com.jasonernst.awm.collectors.WiFiDirectStatsCollector;
import com.jasonernst.awm.loggers.DatabaseLogger;
import com.jasonernst.awm.loggers.NetworkLogger;
import com.jasonernst.awm.loggers.StatsLogger;
import com.jasonernst.awm.stats.BatteryStats;
import com.jasonernst.awm.stats.GPSStats;
import com.jasonernst.awm.stats.NetworkStat;
import lombok.Getter;
import lombok.Setter;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.CHANGE_NETWORK_STATE;
import static android.Manifest.permission.CHANGE_WIFI_MULTICAST_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.WAKE_LOCK;

public class AndroidWirelessStatsCollector {

    @Setter @Getter
    private boolean caching;
    @Getter @Setter
    private boolean wifiUploads;
    @Getter @Setter
    private boolean clearBoot;
    @Getter @Setter
    private boolean clearUpload;
    @Getter @Setter
    private boolean privacy;
    @Getter @Setter
    private String url;

    private boolean firstLaunch;
    private static final String SHARED_PREF_FILE = "uuid.dat";
    private static final String TAG = AndroidWirelessStatsCollector.class.getCanonicalName();
    private Set<StatsCollector> statsCollectors;
    private Set<StatsLogger> statsLoggers;
    private Bus eventBus = BusProvider.getInstance();

    //permission checking code
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 101;
    private RxPermission rxPermission;
    private @NonNull final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private HashMap<String, Permission> permissionResults;

    //all of the permissions that the library needs
    private String[] permissions = {
            BLUETOOTH,
            BLUETOOTH_ADMIN,
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
            ACCESS_WIFI_STATE,
            CHANGE_WIFI_STATE,
            CHANGE_WIFI_MULTICAST_STATE,
            ACCESS_NETWORK_STATE,
            CHANGE_NETWORK_STATE,
            INTERNET,
            WAKE_LOCK,
    };

    @Setter private NetworkLogger networkLogger;
    private DatabaseLogger databaseLogger;

    private ScheduledExecutorService scheduleTaskExecutor;

    @Setter private ObservingDevice thisDevice;
    private SharedPreferences sharedPreferences;

    private WiFiAPStatsCollector wifiStats;
    private BluetoothStatsCollector btStats;

    public AndroidWirelessStatsCollector() {}

    public AndroidWirelessStatsCollector(Activity activity,
                                         boolean caching,
                                         boolean wifiUploads,
                                         boolean clearBoot,
                                         boolean clearUpload,
                                         boolean privacy,
                                         String url) {
        Log.i(TAG, "Initializing Android Wireless Stats Collector");
        firstLaunch = true;
        this.wifiUploads = wifiUploads;
        this.caching = caching;
        this.clearBoot = clearBoot;
        this.clearUpload = clearUpload;
        this.privacy = privacy;
        this.url = url;

        sharedPreferences = activity.getApplicationContext()
                .getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);

        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        UUID uuid;
        String uuidString = sharedPreferences.getString("uuid", null);
        if(uuidString == null) {
            uuid = UUID.randomUUID();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("uuid", uuid.toString());
            editor.apply();
        } else {
            uuid = UUID.fromString(uuidString);
        }

        //detect Android OS version
        String OS = Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.SDK_INT + " "
                + Build.VERSION.RELEASE;
        thisDevice = new ObservingDevice(uuid, OS);

        statsCollectors = new HashSet<>();
        statsLoggers = new HashSet<>();

        rxPermission = RealRxPermission.getInstance(activity.getApplicationContext());

//        GPSStatsCollector gpsStats = new GPSStatsCollector(activity.getApplicationContext());
//        statsCollectors.add(gpsStats);

        btStats = new BluetoothStatsCollector(activity.getApplicationContext());
        statsCollectors.add(btStats);

        wifiStats = new WiFiAPStatsCollector(activity.getApplicationContext());
        statsCollectors.add(wifiStats);

        WiFiDirectStatsCollector wifiDirectStats = new WiFiDirectStatsCollector(activity.getApplicationContext());
        statsCollectors.add(wifiDirectStats);

        InternetStatsCollector itStats = new InternetStatsCollector(activity.getApplicationContext());
        statsCollectors.add(itStats);

        BatteryStatsCollector bStats = new BatteryStatsCollector(activity.getApplicationContext());
        statsCollectors.add(bStats);

        networkLogger = new NetworkLogger(activity.getApplicationContext(), privacy, wifiUploads, url);
        statsLoggers.add(networkLogger);

        databaseLogger = new DatabaseLogger(activity.getApplicationContext(), networkLogger, clearBoot, clearUpload);
        statsLoggers.add(databaseLogger);

//        if (!checkPlayServices(activity)) {
//            Log.d(TAG, "Missing Google Play Services - GPS likely won't work.");
//        }
    }

    public void start() {
        if(firstLaunch) {
            Log.i(TAG, "Checking permissions");
            permissionResults = new HashMap<>();
            compositeDisposable.add(rxPermission.requestEach(permissions)
                    .subscribe(permission -> {
                        Log.i(TAG, permission.name() + " " + permission.state());
                        permissionResults.put(permission.name(), permission);

                        //see if we've received all the permissions yet
                        if(permissionResults.size() == permissions.length) {
                            firstLaunch = false;
                            startLoggers();
                            startStats();
                        }
                    }));
            eventBus.register(this);
        } else {
            startLoggers();
            startStats();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     *
    private boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }*/

    private void startLoggers() {
        Log.i(TAG, "Starting stats loggers");
        for(StatsLogger statsLogger : statsLoggers) {
            try {
                statsLogger.start();
            } catch(Exception ex) {
                Log.e(TAG, "Exception starting the stats logger: " + ex.toString());
                ex.printStackTrace();
            }
        }
    }

    private void startStats() {
        Log.i(TAG, "Starting stats collection");
        for(StatsCollector statsInterface : statsCollectors) {
            statsInterface.setPermissions(permissionResults);
            try {
                statsInterface.start();
            } catch(Exception ex) {
                Log.d(TAG, "Exception starting the stats collection: " + ex.toString());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Stops collecting records but allows to continue to upload
     */
    public void pause() {
        Log.i(TAG, "Stopping stats collection");
        for(StatsCollector statsInterface : statsCollectors) {
            statsInterface.stop();
        }
    }

    public void unpause() {
        Log.i(TAG, "Stopping stats collection");
        for(StatsCollector statsInterface : statsCollectors) {
            try {
                statsInterface.start();
            } catch(Exception ex) {
                Log.d(TAG, "Exception starting the stats collection: " + ex.toString());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Stops collecting and uploading
     */
    public void stop() {
        try {
            eventBus.unregister(this);
            compositeDisposable.clear();

            Log.i(TAG, "Stopping stats collection");
            for (StatsCollector statsInterface : statsCollectors) {
                statsInterface.stop();
            }

            for (StatsLogger statsLogger : statsLoggers) {
                statsLogger.stop();
            }
        } catch(Exception ex) {
            //eat any exception that happens because we might have already stopped
        }
    }

    private void logNetwork(NetworkStat networkStat, ObservingDevice device) {
        new Thread(() -> {
            networkLogger.log(networkStat, device);
        }).start();
    }

    private void logDatabase(NetworkStat networkStat, ObservingDevice device) {
        new Thread(() -> {
            databaseLogger.log(networkStat, thisDevice);
            Log.d(TAG, "LOGGED IN DB: " + databaseLogger.getTotalCount() + " records");
            Log.d(TAG, "UPLOADED: " + databaseLogger.getCountUploaded());
            Log.d(TAG, "NON-UPLOADED: " + databaseLogger.getCountUploaded());
        }).start();
    }

    @Subscribe
    public void updateNetworkStats(NetworkStat networkStat) {

        //if we aren't caching, lets send it straight to the network
        if (!caching) {
            //if we are only doing Wi-Fi uploads let's check if we're online
            if (wifiUploads && networkLogger.isWifiConnected() && networkLogger.isOnline()) {
                logNetwork(networkStat, thisDevice);
            } else if(!wifiUploads && networkLogger.isOnline()) {
                logNetwork(networkStat, thisDevice);
            } else {
                //offline - TODO: memory caching?
            }
        } else {
            logDatabase(networkStat, thisDevice);
        }
    }

    @Subscribe
    public void updateBattery(BatteryStats batteryStats) {
        thisDevice.setBattery(batteryStats.getBatteryPercent());
    }

    @Subscribe
    public void updateGPS(GPSStats gpsStats) {
        thisDevice.updatePosition(gpsStats);
    }

    public int getSavedRecordCount() {
        return databaseLogger.getCountNonUploaded();
    }

    public int getUploadedRecordCount() { return databaseLogger.getCountUploaded(); }
}
