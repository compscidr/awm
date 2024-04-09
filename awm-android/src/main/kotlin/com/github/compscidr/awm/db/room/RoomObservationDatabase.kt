package com.github.compscidr.awm.db.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jasonernst.awm_common.db.ObservationType

@Database(entities = [ObservationEntity::class, BLEObservationEntity::class], version = 4, exportSchema = false)
abstract class RoomObservationDatabase : RoomDatabase() {
    abstract fun bluetoothDao(): BluetoothDao

    companion object {
        private const val DATABASE_NAME = "observation_database.db"
        private var instance: RoomObservationDatabase? = null

        fun getInstance(context: Context): RoomObservationDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomObservationDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }

    fun daoMap(): Map<ObservationType, ObservationDao<*>> {
        return mapOf(
            ObservationType.BLE to bluetoothDao()
        )
    }
}