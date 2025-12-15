package com.example.planty

import android.app.Application
import com.example.planty.data.database.datastore.PlantRepository
import com.example.planty.data.database.datastore.SettingsRepository
import com.example.planty.data.database.AppDatabase

class PlantyApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val plantRepository by lazy {
        PlantRepository(database.getPlantDao())
    }

    val settingsRepository by lazy {
        SettingsRepository(this)
    }
}