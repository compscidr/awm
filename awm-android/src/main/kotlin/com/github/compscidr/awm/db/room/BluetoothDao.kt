package com.github.compscidr.awm.db.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BluetoothDao: ObservationDao<BLEObservationRoomEntity> {
    @Insert
    fun insert(entity: BLEObservationRoomEntity)

    @Delete
    fun delete(entity: BLEObservationRoomEntity): Int

    @Query("SELECT COUNT(*) FROM BLEObservationRoomEntity")
    override fun getNumEntries(): LiveData<Int>

    @Query("SELECT * FROM BLEObservationRoomEntity ORDER BY timestampUTCMillis ASC LIMIT 1")
    override fun getOldest(): LiveData<BLEObservationRoomEntity?>
}