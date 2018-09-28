package io.rightmesh.awm;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import com.vanniktech.rxpermission.Permission;
import com.vanniktech.rxpermission.RealRxPermission;
import com.vanniktech.rxpermission.RxPermission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class AndroidWirelessStatsCollector {

    private static final String TAG = AndroidWirelessStatsCollector.class.getCanonicalName();
    private Set<StatsCollector> statsCollectors;
    private StatsLogger statsLogger;


    //permission checking code
    private RxPermission rxPermission;
    @NonNull final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private HashMap<String, Permission> permissionResults;
    //all of the permissions that the library needs
    String[] permissions = { Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.INTERNET
    };

    public AndroidWirelessStatsCollector(Context context) {
        Log.i(TAG, "Initializing Android Wireless Stats Collector");

        statsCollectors = new HashSet<>();
        rxPermission = RealRxPermission.getInstance(context);

        GPSStatsCollector gpsStats = new GPSStatsCollector(context);
        statsCollectors.add(gpsStats);

        BluetoothStatsCollector btStats = new BluetoothStatsCollector(context);
        statsCollectors.add(btStats);

        statsLogger = new StatsLogger();
    }

    public void start() {
        Log.i(TAG, "Checking permissions");
        permissionResults = new HashMap<>();
        compositeDisposable.add(rxPermission.requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override public void accept(final Permission permission) {
                        Log.i(TAG, permission.name() + " " + permission.state());
                        permissionResults.put(permission.name(), permission);

                        //see if we've received all the permissions yet
                        if(permissionResults.size() == permissions.length) {
                            startStats();
                        }
                    }
                }));
        statsLogger.start();
    }

    private void startStats() {
        Log.i(TAG, "Starting stats collection");
        for(StatsCollector statsInterface : statsCollectors) {
            statsInterface.setPermissions(permissionResults);

            try {
                statsInterface.start();
            } catch(Exception ex) {
                Log.d(TAG, "Exception starting the stats collection: " + ex.toString());
            }
        }
    }

    public void stop() {
        compositeDisposable.clear();

        Log.i(TAG, "Stopping stats collection");
        for(StatsCollector statsInterface : statsCollectors) {
            statsInterface.stop();
        }

        statsLogger.stop();
    }
}
