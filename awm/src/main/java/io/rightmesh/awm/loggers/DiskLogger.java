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
import java.util.concurrent.locks.ReentrantLock;

import io.rightmesh.awm.ObservingDevice;
import io.rightmesh.awm.stats.NetworkDevice;
import io.rightmesh.awm.stats.NetworkStat;
import lombok.Setter;

public class DiskLogger implements StatsLogger {

    private static final String TAG = DiskLogger.class.getCanonicalName();
    private static final String FILENAME = "data.dat";
    private Context context;
    private ReentrantLock lock;
    private Bus eventBus = BusProvider.getInstance();
    private boolean cleanFile;
    private boolean cleanFileAfterUpload;

    /* settable for tests */
    @Setter private BufferedWriter bufferedWriter;
    @Setter private BufferedReader bufferedReader;

    public DiskLogger(Context context, boolean cleanFile, boolean cleanFileAfterUpload) {
        this.context = context;
        this.cleanFile = cleanFile;
        this.cleanFileAfterUpload = cleanFileAfterUpload;
        lock  = new ReentrantLock();
    }

    @Override public void start() {
        try {
            if (cleanFile) {
                context.deleteFile(FILENAME);
            }
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    context.openFileOutput(FILENAME, Context.MODE_PRIVATE)));

            bufferedReader = new BufferedReader(new InputStreamReader(
                    context.openFileInput(FILENAME)));

        } catch(IOException ex) {
            Log.d(TAG, "Failed to open files for logging: " + ex.toString());
            ex.printStackTrace();
        }

        eventBus.register(this);
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
        eventBus.unregister(this);
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

        if(thisDevice.getPosition().latitude == 0 || thisDevice.getPosition().longitude == 0) {
            Log.d(TAG, "null position. ignoring this measure");
            return;
        }

        lock.lock();
        try {
            String deviceJson = "";
            int count = 1;
            for(NetworkDevice network : stat.getDevices()) {
                deviceJson += "\t\t\t{\n";
                deviceJson += "\t\t\t\t\"mac_address\": \"" + network.getMac() + "\",\n";
                deviceJson += "\t\t\t\t\"signal_strength\": " + network.getSignalStrength() + ",\n";
                deviceJson += "\t\t\t\t\"frequency\": " + network.getFrequency() + ",\n";
                deviceJson += "\t\t\t\t\"channel_width\": " + network.getChannelWidth() + ",\n";
                deviceJson += "\t\t\t\t\"security\": \"" + network.getSecurity() + "\",\n";
                if(stat.getType() == NetworkStat.DeviceType.BLUETOOTH) {
                    deviceJson += "\t\t\t\t\"mac_type\": " + 0 + ",\n";
                } else if (stat.getType() == NetworkStat.DeviceType.WIFI) {
                    deviceJson += "\t\t\t\t\"mac_type\": " + 1 + ",\n";
                } else {
                    deviceJson += "\t\t\t\t\"mac_type\": " + -1 + ",\n";
                }
                deviceJson += "\t\t\t\t\"network_name\": \"" + network.getName() + "\"\n";
                if(count < stat.getDevices().size()) {
                    deviceJson += "\t\t\t},\n";
                } else {
                    deviceJson += "\t\t\t}\n";
                }
                count++;
            }

            String jsondata = thisDevice.prepareJSON(deviceJson, new Timestamp(new Date().getTime()));
            bufferedWriter.write(jsondata);
            bufferedWriter.flush();

            eventBus.post(new LogEvent(LogEvent.EventType.SUCCESS, LogEvent.LogType.DISK, 1));
        } finally {
            lock.unlock();
        }
    }

    public int getLogCount() throws IOException {
        while(lock.isLocked()) {
            try {
                Thread.sleep(10);
            } catch(InterruptedException ex) {
                //
            }
        }
        lock.lock();
        int count = 0;
        try {
            String jsondata = "";
            String line = bufferedReader.readLine();
            while (line != null) {
                jsondata = jsondata.concat(line);
                if (line.equals("}")) {
                    count++;
                    jsondata = "";
                }
                line = bufferedReader.readLine();
            }
        } finally {
            lock.unlock();
        }
        return count;
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
        if(!cleanFileAfterUpload) {
            return;
        }

        if(logEvent.getLogType() == LogEvent.LogType.NETWORK) {
            if(logEvent.getEventType() == LogEvent.EventType.SUCCESS ||
                    logEvent.getEventType() == LogEvent.EventType.MALFORMED) {
                try {
                    lock.lock();
                    bufferedReader.close();
                    bufferedWriter.close();
                    if(context.deleteFile("data.dat")) {
                        Log.d(TAG, "Successfully cleared the old data file.");
                    } else {
                        Log.d(TAG, "Failed to clear the old data file :(");
                    }
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
}
