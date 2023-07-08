package com.github.compscidr.awm.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BLEObservationDao {

    @Insert
    fun insert(entity: BLEObservationEntity)

    @Delete
    fun delete(entity: BLEObservationEntity)

    @Query("SELECT COUNT(*) FROM BLEObservationEntity")
    fun getNumEntries(): LiveData<Int>

    @Query("SELECT * FROM BLEObservationEntity ORDER BY timestampUTCMillis ASC LIMIT 1")
    fun getOldest(): BLEObservationEntity?

    @Query("SELECT COUNT(*) FROM BLEObservationEntity")
    fun size(): Int
}