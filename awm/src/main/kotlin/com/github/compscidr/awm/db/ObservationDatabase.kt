package com.github.compscidr.awm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [BLEObservationEntity::class], version = 4, exportSchema = false)
abstract class ObservationDatabase : RoomDatabase() {
    abstract fun bleObservationDao(): BLEObservationDao

    companion object {
        private const val DATABASE_NAME = "observation_database"
        private var instance: ObservationDatabase? = null

        fun getInstance(context: Context): ObservationDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    ObservationDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }
}