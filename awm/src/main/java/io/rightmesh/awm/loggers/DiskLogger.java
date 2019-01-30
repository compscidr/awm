package io.rightmesh.awm.loggers;

import android.content.Context;
import android.util.Log;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;
import com.anadeainc.rxbus.Subscribe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import io.rightmesh.awm.ObservingDevice;
import io.rightmesh.awm.stats.BluetoothStats;
import io.rightmesh.awm.stats.NetworkStat;
import io.rightmesh.awm.stats.WiFiStats;

public class DiskLogger implements StatsLogger {
    private static final String TAG = DiskLogger.class.getCanonicalName();
    private Context context;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private ReentrantLock lock;
    private Bus eventBus = BusProvider.getInstance();
    private boolean cleanfile = false;

    public DiskLogger(Context context, boolean cleanFile) {
        this.context = context;
        this.cleanfile = cleanFile;
        lock  = new ReentrantLock();
    }

    @Override public void start() {
        try {
            if(cleanfile) {
                context.deleteFile("data.dat");
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                        context.openFileOutput("data.dat", Context.MODE_PRIVATE)));
            } else {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                        context.openFileOutput("data.dat", Context.MODE_APPEND)));
            }

            bufferedReader = new BufferedReader(new InputStreamReader(
                    context.openFileInput("data.dat")));
            eventBus.register(this);
        } catch(IOException ex) {
            Log.d(TAG, "Failed to open files for logging: " + ex.toString());
            ex.printStackTrace();
        }
    }

    @Override public void stop() {
        try {
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
            if(bufferedReader != null) {
                bufferedReader.close();
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This should be called every time there is new data to be logged. It will be timestamped with
     * the current time when written to the file. It will append the results to the end of the file.
     *
     * @param stat the particular observation to record.
     * @param thisDevice the current device making the observation
     * @throws IOException if there is a problem writing to the output file.
     */
    @Override
    public void log(NetworkStat stat, ObservingDevice thisDevice) throws IOException {
        lock.lock();
        try {
            String deviceJson = "";
            int count = 1;
            Set<String> macs = stat.getMacs();
            for (String mac : macs) {
                deviceJson += "\t\t\t{\n";
                deviceJson += "\t\t\t\t\"mac_address\": \"" + mac + "\",\n";
                if (stat instanceof BluetoothStats) {
                    deviceJson += "\t\t\t\t\"mac_type\": " + 0 + ",\n";
                } else if (stat instanceof WiFiStats) {
                    deviceJson += "\t\t\t\t\"mac_type\": " + 1 + ",\n";
                } else {
                    deviceJson += "\t\t\t\t\"mac_type\": " + -1 + ",\n";
                }
                deviceJson += "\t\t\t\t\"network_name\": \"" + stat.getName() + "\"\n";
                if (count < macs.size()) {
                    deviceJson += "\t\t\t},\n";
                } else {
                    deviceJson += "\t\t\t}\n";
                }
                count++;
            }

            String jsondata = thisDevice.prepareJSON(deviceJson, new Timestamp(new Date().getTime()));
            bufferedWriter.write(jsondata);
            bufferedWriter.flush();
        } finally {
            lock.unlock();
        }
    }

    public ArrayList<String> getPendingLogs() throws IOException {
        ArrayList<String> data = new ArrayList<>();

        while(lock.isLocked()) {
            try {
                Thread.sleep(10);
            } catch(InterruptedException ex) {
                 //
            }
        }

        lock.lock();
        try {
            String jsondata = "";
            String line = bufferedReader.readLine();
            int count = 0;
            while (line != null) {
                jsondata = jsondata.concat(line);
                if (line.equals("}")) {
                    data.add(jsondata + "\n");
                    Log.d(TAG, "JSONDATA: " + jsondata + "\n");
                    count++;
                    jsondata = "";
                }
                line = bufferedReader.readLine();
            }
            if (data.size() > 0) {
                Log.d(TAG, "RECORDS: " + count);
            }
        } finally {
            lock.unlock();
        }
        return data;
    }

    @Subscribe
    public void networkUploadStatus(LogEvent logEvent) {
        if(logEvent.getType() == LogEvent.EventType.SUCCESS ||
                logEvent.getType() == LogEvent.EventType.MALFORMED) {
            try {
                lock.lock();
                bufferedReader.close();
                bufferedWriter.close();
                context.deleteFile("data.dat");
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                        context.openFileOutput("data.dat", Context.MODE_PRIVATE)));
                bufferedReader = new BufferedReader(new InputStreamReader(
                        context.openFileInput("data.dat")));
            } catch(IOException ex) {
                Log.e(TAG, "Failed recreating the output file on clean: " + ex.toString());
            } finally {
                lock.unlock();
            }
        } else {
            Log.d(TAG, "Failed to upload stats. leaving them in file.");
        }
    }
}
