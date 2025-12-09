package com.example.planty.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.planty.data.database.AppDatabase
import java.util.concurrent.TimeUnit

class WateringWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val notificationService = NotificationService(applicationContext)

        val allPlants = database.getPlantDao().getAllPlantsList()

        val plantsToWater = allPlants.filter { plant ->
            shouldWater(plant.lastWatered, plant.wateringFrequencyDays)
        }

        if (plantsToWater.isNotEmpty()) {
            notificationService.createNotificationChannel()

            plantsToWater.forEach { plant ->
                notificationService.showWateringNotification(plant.id, plant.name)
            }
        }

        return Result.success()
    }

    private fun shouldWater(lastWatered: Long, frequencyDays: Int): Boolean {
        val today = System.currentTimeMillis()
        val diffInMillis = today - lastWatered
        val daysPassed = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        return daysPassed >= frequencyDays
    }
}