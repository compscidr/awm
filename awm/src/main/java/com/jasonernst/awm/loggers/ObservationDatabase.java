package com.jasonernst.awm.loggers;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DatabaseObservation.class}, version = 1)
public abstract class ObservationDatabase extends RoomDatabase {
    public abstract DatabaseObservationDao databaseObservationDao();
}
