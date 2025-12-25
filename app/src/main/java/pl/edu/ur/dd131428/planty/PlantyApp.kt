package pl.edu.ur.dd131428.planty

import android.app.Application
import pl.edu.ur.dd131428.planty.data.database.datastore.PlantRepository
import pl.edu.ur.dd131428.planty.data.database.datastore.SettingsRepository
import pl.edu.ur.dd131428.planty.data.database.AppDatabase

class PlantyApp : Application() {

    val database by lazy { AppDatabase.Companion.getDatabase(this) }

    val plantRepository by lazy {
        PlantRepository(database.getPlantDao())
    }

    val settingsRepository by lazy {
        SettingsRepository(this)
    }
}