package com.jasonernst.awm_example;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;
import com.anadeainc.rxbus.Subscribe;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.jasonernst.awm.stats.GPSStats;
import com.jasonernst.awm.stats.NetworkStat;

import java.util.ArrayList;

import io.rightmesh.awm_lib_example.R;

public class MapFragment extends Fragment {
    private static final String TAG = MapFragment.class.getCanonicalName();
    private static final int DEFAULT_ZOOM = 20;
    private static final int DEFAULT_RADIUS = 50; //between 10 and 50

    private HeatmapTileProvider mProvider;
    private GPSStats currentPosition;
    private TileOverlay mOverlay;
    private ArrayList<WeightedLatLng> data = new ArrayList<WeightedLatLng>();
    protected Bus eventBus = BusProvider.getInstance();
    private MapView mMapView;
    private GoogleMap googleMap;
    private boolean positionSet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately
        positionSet = false;

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                try {
                    mMap.setMyLocationEnabled(true);
                } catch(SecurityException ex) {
                    //if they didnt' give location permission who cares.
                }
                addHeatMap();
            }
        });

        eventBus.register(this);
        return rootView;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        eventBus.unregister(this);
    }

    @Subscribe
    public void updateGPS(GPSStats gpsStats) {
        currentPosition = gpsStats;

        //in case it hasn't init'd yet
        if (googleMap != null && !positionSet) {
            LatLng pos = new LatLng(gpsStats.latitude, gpsStats.longitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM);
            googleMap.animateCamera(cameraUpdate);
            positionSet = true;
        }
    }

    @Subscribe
    public void updateNetworkDevices(NetworkStat networkStat) {

        if (currentPosition == null || networkStat == null || networkStat.getDevices() == null) {
            return;
        }

        try {
            /*
            if (networkStat.getType() == BLUETOOTH) {
                WeightedLatLng point = new WeightedLatLng(new LatLng(currentPosition.latitude, currentPosition.longitude), networkStat.getDevices().size());
            } else if(networkStat.getType() == WIFI) {

            }*/
            WeightedLatLng point = new WeightedLatLng(new LatLng(currentPosition.latitude, currentPosition.longitude), networkStat.getDevices().size());
            data.add(point);

            mProvider.setWeightedData(data);
            mOverlay.clearTileCache();
        } catch(Exception ex) {
            Log.d(TAG, "EX: "+ ex.toString());
        }
    }

    private void addHeatMap() {
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        data.add(new WeightedLatLng(new LatLng(0,0),0));
        mProvider = new HeatmapTileProvider.Builder()
                .radius(DEFAULT_RADIUS)
                .weightedData(data)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
