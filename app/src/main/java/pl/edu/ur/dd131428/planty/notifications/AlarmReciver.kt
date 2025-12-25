package pl.edu.ur.dd131428.planty.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val plantId = intent.getIntExtra("PLANT_ID", -1)
        val plantName = intent.getStringExtra("PLANT_NAME") ?: "Roślina"

        if (plantId != -1) {
            val service = NotificationService(context)
            service.createNotificationChannel()
            service.showWateringNotification(plantId, plantName)
        }
    }
}