package com.example.vrnandr.kpiwatcher.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Kpi::class], version = 1)
abstract class KpiDatabase: RoomDatabase(){
    abstract val kpiDao: KpiDao
    companion object {

        @Volatile
        private var INSTANCE: KpiDatabase? = null

        fun getInstance(context: Context): KpiDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        KpiDatabase::class.java,
                        "kpi_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}