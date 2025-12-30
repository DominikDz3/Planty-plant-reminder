package pl.edu.ur.dd131428.planty.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import pl.edu.ur.dd131428.planty.data.database.entity.Plant
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedulePlantNotification(plant: Plant) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = plant.lastWatered

        // Resetujemy do północy
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Dodajemy pierwszy cykl
        calendar.add(Calendar.DAY_OF_YEAR, plant.wateringFrequencyDays)

        // Ustawiamy godzinę
        val hour = (plant.notificationTime / (1000 * 60 * 60)).toInt()
        val minute = ((plant.notificationTime / (1000 * 60)) % 60).toInt()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // POPRAWKA: Pętla while. Jeśli wyliczona data jest w przeszłości,
        // dodajemy kolejne cykle (dni) tak długo, aż trafimy w przyszłość.
        // To naprawia problem "Termin minął" przy testowaniu.
        val now = System.currentTimeMillis()
        while (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, plant.wateringFrequencyDays)
        }

        // Logowanie daty alarmu (sprawdź w Logcat po tagu "AlarmScheduler")
        Log.d("AlarmScheduler", "Ustawiam alarm dla ${plant.name} na: ${calendar.time}")
        // Toast.makeText(context, "Alarm: ${calendar.time}", Toast.LENGTH_LONG).show()

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("PLANT_ID", plant.id)
            putExtra("PLANT_NAME", plant.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plant.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun cancelNotification(plantId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plantId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}