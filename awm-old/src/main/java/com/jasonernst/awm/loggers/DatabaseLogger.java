package com.jasonernst.awm.loggers;

import android.util.Log;

import com.anadeainc.rxbus.Bus;
import com.anadeainc.rxbus.BusProvider;

import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

import com.jasonernst.awm.ReportingDevice;
import com.jasonernst.awm.stats.NetworkStat;

import lombok.Setter;

public class DatabaseLogger implements StatsLogger {

    private final String TAG = DatabaseLogger.class.getCanonicalName();
    private Bus eventBus = BusProvider.getInstance();
    private NetworkLogger networkLogger;
    private ObservationDatabase db;
    @Setter private volatile boolean running;
    @Setter private Thread networkThread;
    @Setter private boolean clearBoot;
    @Setter private boolean clearUpload;

    public DatabaseLogger(NetworkLogger networkLogger, ObservationDatabase db, boolean clearBoot, boolean clearUpload) {
        this.running = false;
        this.networkLogger = networkLogger;
        this.clearBoot = clearBoot;
        this.clearUpload = clearUpload;
        this.db = db;
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
            if (networkLogger.isOnline() && db.databaseObservationDao().getCountNonUploaded() > 0) {
                Log.d(TAG, "ONLINE and HAVE NON-UPLOADED");
                List<DatabaseObservation> observations = db.databaseObservationDao().getNonUploaded();
                Log.d(TAG, "OBSERVATIONS: " + observations.size());
                for (DatabaseObservation observation : observations) {
                    if(networkThread.isInterrupted()) {
                        return;
                    }
                    try {
                        //Log.d(TAG, "Trying to upload");
                        networkLogger.uploadJsonEntry(observation.getObservationJson());
                        observation.setUploaded(true);
                        if (clearUpload) {
                            db.databaseObservationDao().delete(observation);
                        } else {
                            observation.setUploaded(false);
                            observation.setUploadedSucessfully(true);
                            db.databaseObservationDao().updateObservation(observation);
                        }
                        //Log.d(TAG, "Uploaded record");
                    } catch(InvalidParameterException ex) {
                        Log.d(TAG, "Invalid entry - removing");
                        db.databaseObservationDao().delete(observation);
                    } catch (IOException ex) {
                        //Log.d(TAG, "Failed uploading.");
                    }
                }
            }

            //time between uploads
            try {
                wait(5000);
            } catch(InterruptedException ex) {
                return;
            }
        }
    }

    public void wait(int sleep_ms) throws InterruptedException {
        Thread.sleep(sleep_ms);
    }

    @Override
    public void log(NetworkStat stat, ReportingDevice thisDevice) {

        DatabaseObservation databaseObservation = new DatabaseObservation();
        try {
            databaseObservation.setObservationJson(stat.toJSON());
        } catch (JSONException e) {
            Log.d(TAG, e.toString());
        }

        databaseObservation.setUploaded(false);
        databaseObservation.setUploadedSucessfully(false);

        db.databaseObservationDao().insert(databaseObservation);

        if (db.databaseObservationDao().insert(databaseObservation) > 0) {
            eventBus.post(new LogEvent(LogEvent.EventType.SUCCESS, LogEvent.LogType.DB, 1));
        } else {
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
