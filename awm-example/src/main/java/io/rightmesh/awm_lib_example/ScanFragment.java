package io.rightmesh.awm_lib_example;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;
import com.anadeainc.rxbus.Subscribe;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.rightmesh.awm.loggers.LogEvent;
import io.rightmesh.awm.loggers.WiFiScan;
import io.rightmesh.awm.stats.GPSStats;
import io.rightmesh.awm.stats.NetworkDevice;
import io.rightmesh.awm.stats.NetworkStat;

import static io.rightmesh.awm.stats.NetworkStat.DeviceType.BLUETOOTH;
import static io.rightmesh.awm.stats.NetworkStat.DeviceType.WIFI;

public class ScanFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ScanFragment.class.getCanonicalName();
    protected Bus eventBus = BusProvider.getInstance();

    private TextView txtBtDevices;
    private TextView txtWifiDevices;
    private TextView txtGPS;
    private TextView txtSavedRecords;
    private TextView txtUploadedRecords;
    private TextView txtStatus;
    private MainActivity mainActivity;
    private static int uploads = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        Button btnOff = (Button) view.findViewById(R.id.btnOnOff);
        btnOff.setOnClickListener(this);

        Button btnPause = (Button) view.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(this);

        txtStatus = view.findViewById(R.id.txtStatus);
        txtBtDevices = view.findViewById(R.id.btDevices);
        txtWifiDevices = view.findViewById(R.id.wifiDevices);
        txtGPS = view.findViewById(R.id.gpsCoords);
        txtSavedRecords = view.findViewById(R.id.txtRecords);
        txtUploadedRecords = view.findViewById(R.id.txtUploadedRecords);

        Activity activity = getActivity();
        if(activity != null) {
            mainActivity = (MainActivity)activity;

            new Thread(() -> {
                int savedRecords = mainActivity.getAwsc().getSavedRecordCount();
                int uploadedRecords = mainActivity.getAwsc().getUploadedRecordCount();

                mainActivity.runOnUiThread(() -> {
                    txtSavedRecords.setText("Saved Records: " + savedRecords);
                    txtUploadedRecords.setText("Uploaded Records: " + uploadedRecords);
                });
            }).start();
        }

        eventBus.register(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        txtUploadedRecords.setText("Uploaded Records: " + uploads);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        eventBus.unregister(this);
    }

    @Subscribe
    public void updateNetworkDevices(NetworkStat networkStat) {
        if (networkStat.getType() == BLUETOOTH) {
            Log.d("MA", "GOT BT NETWORK STAT TYPE");
            String status = "btDevices: ";
            status = status + networkStat.getDevices().size();
            for(NetworkDevice device : networkStat.getDevices()) {
                status = status + "\n" + device.getMac() + " " + device.getName() + " "
                        + device.getSignalStrength() + "dB";
            }
            txtBtDevices.setText(status);
        } else if (networkStat.getType() == WIFI) {
            Log.d("MA", "GOT WIFI NETWORK STAT TYPE: " + networkStat.getDevices().size());
            String status = "wifiDevices: ";
            status = status + networkStat.getDevices().size();
            for(NetworkDevice device : networkStat.getDevices()) {
                status = status + "\n" + device.getMac() + " " + device.getName() + " "
                        + device.getFrequency() + "Mhz " + device.getSignalStrength() + "dB";
            }

            txtWifiDevices.setText(status);

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                eventBus.post(new WiFiScan());
            }, 5, TimeUnit.SECONDS);
        } else {
            Log.d("MA", "GOT UNKNOWN NETWORK STAT TYPE");
        }
    }

    @Subscribe
    public void updateGPS(GPSStats gpsStats) {
        txtGPS.setText("GPS Position: long:" + gpsStats.longitude + " lat: " + gpsStats.latitude);
    }

    @Subscribe
    public void logEvent(LogEvent logEvent) {
        new Thread(() -> {

            if(logEvent.getLogType() == LogEvent.LogType.DB
                    && logEvent.getEventType() == LogEvent.EventType.FAILURE) {
                mainActivity.runOnUiThread(() -> {
                    txtStatus.setText("Error saving log to dB. Storage probably full.");
                    Button btnPause = getView().findViewById(R.id.btnPause);
                    btnPause.callOnClick();
                });
            }

            int savedRecords = mainActivity.getAwsc().getSavedRecordCount();

            //this will capture only the session uploads in the case where we are clearing them
            if(logEvent.getLogType() == LogEvent.LogType.NETWORK
                    && logEvent.getEventType() == LogEvent.EventType.SUCCESS) {
                uploads++;
            }
            //this will capture any uploads where the data hasn't been cleared
            int uploadedRecords = mainActivity.getAwsc().getUploadedRecordCount();

            mainActivity.runOnUiThread(()-> {
                txtSavedRecords.setText("Saved Records: " + savedRecords);
                if (mainActivity.clearUploads()) {
                    txtUploadedRecords.setText("Uploaded Records: " + uploads);
                } else {
                    txtUploadedRecords.setText("Uploaded Records: " + uploadedRecords);
                }
            });
        }).start();
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOnOff) {
            Button btnOnOff = getView().findViewById(R.id.btnOnOff);
            if (btnOnOff.getText().equals("TURN OFF")) {
                Log.d("MA", "TURN OFF");
                mainActivity.stop();
                txtBtDevices.setText("Turned off.");
                txtWifiDevices.setText("Turned off.");
                btnOnOff.setText("TURN ON");
            } else {
                Log.d("MA", "TURN ON");
                mainActivity.start();
                txtBtDevices.setText("btDevices: ");
                txtWifiDevices.setText("wifiDevices: ");
                btnOnOff.setText("TURN OFF");
            }
        } else if(v.getId() == R.id.btnPause) {
            Button btnPause = getView().findViewById(R.id.btnPause);
            if (btnPause.getText().equals("PAUSE")) {
                Log.d("MA", "PAUSED");
                mainActivity.getAwsc().pause();
                txtBtDevices.setText("Paused.");
                txtWifiDevices.setText("Paused.");
                btnPause.setText("UNPAUSE");
            } else {
                Log.d("MA", "UNPAUSED");
                mainActivity.getAwsc().unpause();
                txtBtDevices.setText("btDevices: ");
                txtWifiDevices.setText("wifiDevices: ");
                btnPause.setText("PAUSE");
            }
        }
    }
}
