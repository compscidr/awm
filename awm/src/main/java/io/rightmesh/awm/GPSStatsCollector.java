package io.rightmesh.awm;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class GPSStatsCollector extends StatsCollector {

    private FusedLocationProviderClient mFusedLocationClient;

    GPSStatsCollector(Context context) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    void start() throws Exception {
        mFusedLocationClient.requestLocationUpdates()
    }

    @Override
    void stop() {

    }
}
