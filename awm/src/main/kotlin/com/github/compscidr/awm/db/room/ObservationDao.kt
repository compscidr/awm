package com.github.compscidr.awm.db.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ObservationDao<T> {

    @Insert
    fun insert(entity: ObservationEntity)

    @Delete
    fun delete(entity: ObservationEntity)

    @Query("SELECT COUNT(*) FROM ObservationEntity")
    fun getNumEntries(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM ObservationEntity WHERE observationType = :observationType")
    fun getNumEntries(observationType: Int): LiveData<Int>

    @Query("SELECT * FROM ObservationEntity ORDER BY timestampUTCMillis ASC LIMIT 1")
    fun getOldest(): ObservationEntity?

    @Query("SELECT * FROM ObservationEntity WHERE observationType = :observationType ORDER BY timestampUTCMillis ASC LIMIT 1")
    fun getOldest(observationType: Int): ObservationEntity?

    @Query("SELECT COUNT(*) FROM ObservationEntity")
    fun size(): Int
}