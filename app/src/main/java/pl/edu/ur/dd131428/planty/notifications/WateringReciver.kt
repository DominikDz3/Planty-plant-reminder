package pl.edu.ur.dd131428.planty.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import pl.edu.ur.dd131428.planty.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WateringReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val plantId = intent.getIntExtra("plantId", -1)
        val notificationId = intent.getIntExtra("notificationId", -1)

        if (plantId != -1) {
            val pendingResult = goAsync()
            val database = AppDatabase.Companion.getDatabase(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val plant = database.getPlantDao().getAllPlantsList().find { it.id == plantId }

                    if (plant != null) {
                        val updatedPlant = plant.copy(lastWatered = System.currentTimeMillis())
                        database.getPlantDao().update(updatedPlant)
                    }

                    if (notificationId != -1) {
                        NotificationManagerCompat.from(context).cancel(notificationId)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
