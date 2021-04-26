package com.jasonernst.awm.loggers;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {DatabaseObservation.class}, version = 1)
public abstract class ObservationDatabase extends RoomDatabase {
    public abstract DatabaseObservationDao databaseObservationDao();
}
