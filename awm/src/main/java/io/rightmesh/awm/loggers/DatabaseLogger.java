package io.rightmesh.awm.loggers;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;

import org.json.JSONException;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import io.rightmesh.awm.ObservingDevice;
import io.rightmesh.awm.stats.NetworkDevice;
import io.rightmesh.awm.stats.NetworkStat;

public class DatabaseLogger implements StatsLogger {

    private final String TAG = DatabaseLogger.class.getCanonicalName();
    private Bus eventBus = BusProvider.getInstance();
    private NetworkLogger networkLogger;
    private ObservationDatabase db;
    private volatile boolean running;
    private Thread networkThread;
    private boolean clearBoot;
    private boolean clearUpload;

    public DatabaseLogger(Context context, NetworkLogger networkLogger, boolean clearBoot, boolean clearUpload) {
        this.running = false;
        this.networkLogger = networkLogger;
        this.clearBoot = clearBoot;
        this.clearUpload = clearUpload;
        this.db = Room.databaseBuilder(context,
                ObservationDatabase.class, "observation-database").build();
    }

    @Override
    public void start() throws Exception {
        running = true;

        //clobber the dB
        if (clearBoot) {
            db.clearAllTables();
        }

        networkThread = new Thread(this::uploadLogs);
        networkThread.start();
    }

    @Override
    public void stop() {
        running = false;
        networkThread.interrupt();
    }

    public void uploadLogs() {
        Log.d(TAG, "UPLOAD LOGS STARTED IN DBLOGGER");
        while (running && !networkThread.isInterrupted()) {
            Log.d(TAG, "INSIDE DBLOGGER UPLOAD LOOP");
            if (networkLogger.isOnline() && networkLogger.isWifiConnected()
                    && db.databaseObservationDao().getCountNonUploaded() > 0) {
                Log.d(TAG, "ONLINE and HAVE NON-UPLOADED");
                List<DatabaseObservation> observations = db.databaseObservationDao().getNonUploaded();
                Log.d(TAG, "OBSERVATIONS: " + observations.size());
                for (DatabaseObservation observation : observations) {
                    if(networkThread.isInterrupted()) {
                        return;
                    }
                    try {
                        Log.d(TAG, "Trying to upload");
                        observation.setUploaded(true);
                        networkLogger.uploadJsonEntry(observation.getObservationJson());
                        if (clearUpload) {
                            db.databaseObservationDao().delete(observation);
                        } else {
                            observation.setUploaded(false);
                            observation.setUploadedSucessfully(true);
                            db.databaseObservationDao().updateObservation(observation);
                        }
                        Log.d(TAG, "Uploaded record");
                    } catch (IOException ex) {
                        Log.d(TAG, "Failed uploading.");
                    }
                }
            }

            //time between uploads
            try {
                Thread.sleep(5000);
            } catch(InterruptedException ex) {
                return;
            }
        }
    }

    @Override
    public void log(NetworkStat stat, ObservingDevice thisDevice) {

        DatabaseObservation databaseObservation = new DatabaseObservation();
        try {
            databaseObservation.setObservationJson(stat.toJSON(thisDevice));
        } catch (JSONException e) {
            Log.d(TAG, e.toString());
        }

        databaseObservation.setUploaded(false);
        databaseObservation.setUploadedSucessfully(false);

        try {
            db.databaseObservationDao().insert(databaseObservation);
            eventBus.post(new LogEvent(LogEvent.EventType.SUCCESS, LogEvent.LogType.DB, 1));
        } catch( Exception ex ) {
            //this occurs if the storage space is full
            eventBus.post(new LogEvent(LogEvent.EventType.FAILURE, LogEvent.LogType.DB, 1));
        }
    }

    public int getCountUploaded() {
        return db.databaseObservationDao().getCountUploaded();
    }

    public int getCountNonUploaded() {
        return db.databaseObservationDao().getCountNonUploaded();
    }

    public int getTotalCount() {
        return db.databaseObservationDao().getCount();
    }
}
