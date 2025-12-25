package pl.edu.ur.dd131428.planty.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import pl.edu.ur.dd131428.planty.MainActivity
import pl.edu.ur.dd131428.planty.R
import pl.edu.ur.dd131428.planty.data.database.AppDatabase
import pl.edu.ur.dd131428.planty.data.database.dao.PlantDao
import pl.edu.ur.dd131428.planty.data.database.entity.Plant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlantWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.Companion.getDatabase(context)
            val dao: PlantDao = database.getPlantDao()

            val plants: List<Plant> = try {
                dao.getAllPlants().first()
            } catch (e: Exception) {
                emptyList()
            }

            val now = System.currentTimeMillis()
            var plantsToWaterCount = 0

            plants.forEach { plant ->
                val nextWateringDate = plant.lastWatered + (plant.wateringFrequencyDays * 24 * 60 * 60 * 1000L)
                if (nextWateringDate <= now) {
                    plantsToWaterCount++
                }
            }

            val views = RemoteViews(context.packageName, R.layout.plant_widget)

            if (plantsToWaterCount > 0) {
                views.setTextViewText(R.id.widget_status_text, "Do podlania: $plantsToWaterCount 🌱")
            } else {
                views.setTextViewText(R.id.widget_status_text, "Wszystkie podlane! 💧")
            }

            val intent = Intent(context, MainActivity::class.java)
            val flags =
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                flags
            )

            views.setOnClickPendingIntent(R.id.widget_status_text, pendingIntent)
            views.setOnClickPendingIntent(R.id.widget_icon, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}