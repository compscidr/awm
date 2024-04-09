package com.github.compscidr.awm.db.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BluetoothDao: ObservationDao<BLEObservationEntity> {
    @Insert
    fun insert(entity: BLEObservationEntity)

    @Delete
    fun delete(entity: BLEObservationEntity): Int

    @Query("SELECT COUNT(*) FROM BLEObservationEntity")
    override fun getNumEntries(): LiveData<Int>

    @Query("SELECT * FROM BLEObservationEntity ORDER BY timestampUTCMillis ASC LIMIT 1")
    override fun getOldest(): LiveData<BLEObservationEntity?>
}