package pl.edu.ur.dd131428.planty.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pl.edu.ur.dd131428.planty.MainActivity
import pl.edu.ur.dd131428.planty.R

class NotificationService(private val context: Context) {

    private val channelId = "water_reminders"
    private val channelName = "Przypomnienia o podlewaniu"

    fun createNotificationChannel() {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Powiadomienia o konieczności podlania roślin"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
    }

    fun showWateringNotification(plantId: Int, plantName: String) {

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("plantId_from_notification", plantId)
        }

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            plantId,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val actionIntent = Intent(context, WateringReceiver::class.java).apply {
            putExtra("plantId", plantId)
            putExtra("notificationId", plantId)
        }

        val actionPendingIntent = PendingIntent.getBroadcast(
            context,
            plantId,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Czas podlać: $plantName")
            .setContentText("Twoja roślinka potrzebuje wody!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)

            .addAction(
                R.drawable.ic_launcher_foreground,
                "Podlano",
                actionPendingIntent
            )

        try {
            NotificationManagerCompat.from(context).notify(plantId, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}