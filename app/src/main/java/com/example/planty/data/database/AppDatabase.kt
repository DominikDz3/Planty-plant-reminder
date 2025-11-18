package com.example.planty.data.database

import android.content.Context
import com.example.planty.data.database.entity.Plant
import com.example.planty.data.database.entity.WaterHistory
import com.example.planty.data.database.dao.PlantDao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.planty.data.database.dao.WaterHistoryDao


@Database(version = 1, entities = [Plant::class, WaterHistory::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun getPlantDao(): PlantDao
    abstract fun getWaterHistoryDao(): WaterHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Planty_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}