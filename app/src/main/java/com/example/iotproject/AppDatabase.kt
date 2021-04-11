package com.example.iotproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.iotproject.fragments.activity.Activity
import com.example.iotproject.fragments.activity.ActivityDao
import com.example.iotproject.fragments.gate.Gate
import com.example.iotproject.fragments.gate.GateDao


@Database(entities = arrayOf(Gate::class, Activity::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gateDao() : GateDao
    abstract fun activityDao() : ActivityDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "word_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}