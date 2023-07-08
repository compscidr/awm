package com.jasonernst.awm.loggers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DatabaseObservationDao {

    @Query("SELECT * FROM dataobservation")
    List<DatabaseObservation> getAll();

    @Query("SELECT * FROM dataobservation WHERE uploaded = 1")
    List<DatabaseObservation> getUploaded();

    @Query("SELECT * FROM dataobservation WHERE uploaded = 0")
    List<DatabaseObservation> getNonUploaded();

    @Query("SELECT COUNT(*) FROM dataobservation WHERE uploadedsuccesfully = 0")
    int getCountNonUploaded();

    @Query("SELECT COUNT(*) FROM dataobservation WHERE uploadedsuccesfully = 1")
    int getCountUploaded();

    @Query("SELECT COUNT(*) FROM dataobservation WHERE uploaded = 1")
    int getCountInProgress();

    @Query("SELECT COUNT(*) FROM dataobservation")
    int getCount();

    @Query("SELECT * FROM dataobservation WHERE id = :userid")
    DatabaseObservation getById(int userid);

    @Insert
    long[] insertAll(DatabaseObservation ... databaseObservations);

    @Insert
    long insert(DatabaseObservation databaseObservation);

    @Delete
    void delete(DatabaseObservation databaseObservation);

    @Update
    void updateObservation(DatabaseObservation ... databaseObservations);
}
