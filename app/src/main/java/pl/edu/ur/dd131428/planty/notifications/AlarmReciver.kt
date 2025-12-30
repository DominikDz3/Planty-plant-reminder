package pl.edu.ur.dd131428.planty.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val plantId = intent.getIntExtra("PLANT_ID", -1)
        val plantName = intent.getStringExtra("PLANT_NAME") ?: "Roślina"

        Log.d("AlarmReceiver", "Otrzymano sygnał alarmu dla: $plantName")

        if (plantId != -1) {
            val service = NotificationService(context)
            // Tworzymy kanał (bezpiecznik, gdyby system go usunął)
            service.createNotificationChannel()
            // Wyświetlamy
            service.showWateringNotification(plantId, plantName)
        }
    }
}