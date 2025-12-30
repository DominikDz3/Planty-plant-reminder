package pl.edu.ur.dd131428.planty.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.edu.ur.dd131428.planty.data.database.AppDatabase
import pl.edu.ur.dd131428.planty.data.database.dao.PlantDao

class WateringReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val plantId = intent.getIntExtra("plantId", -1)
        val notificationId = intent.getIntExtra("notificationId", -1)

        if (plantId != -1) {
            // Informujemy system, że będziemy wykonywać dłuższą operację w tle
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val dao: PlantDao = db.getPlantDao()

                    // Pobieramy roślinę (używamy first() aby pobrać aktualną wartość)
                    val plant = try {
                        dao.getPlantById(plantId).first()
                    } catch (e: Exception) {
                        null
                    }

                    if (plant != null) {
                        val now = System.currentTimeMillis()

                        // Aktualizujemy historię (dodajemy dzisiejszą datę)
                        val newHistory = plant.wateringHistory.toMutableList()
                        // Sprawdzamy czy już nie ma wpisu z dzisiaj (żeby nie dublować)
                        // Choć przy powiadomieniach rzadko się to zdarza
                        newHistory.add(now)
                        newHistory.sortDescending()

                        // Tworzymy zaktualizowany obiekt
                        val updatedPlant = plant.copy(
                            lastWatered = now,
                            wateringHistory = newHistory
                        )

                        // Zapisujemy w bazie
                        dao.update(updatedPlant)

                        // Ustawiamy KOLEJNY alarm (cykliczność)
                        val scheduler = AlarmScheduler(context)
                        scheduler.schedulePlantNotification(updatedPlant)

                        // Wyświetlamy potwierdzenie na wątku głównym
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Podlano! 🌱", Toast.LENGTH_SHORT).show()

                            // Zamykamy powiadomienie
                            val notificationManager = androidx.core.app.NotificationManagerCompat.from(context)
                            try {
                                notificationManager.cancel(notificationId)
                            } catch (e: SecurityException) {
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    // Kończymy pracę
                    pendingResult.finish()
                }
            }
        }
    }
}