package pl.edu.ur.dd131428.planty.ui.screens.details

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import pl.edu.ur.dd131428.planty.PlantyApp
import pl.edu.ur.dd131428.planty.data.database.datastore.PlantRepository
import pl.edu.ur.dd131428.planty.data.database.entity.Plant
import pl.edu.ur.dd131428.planty.data.database.entity.TimelineEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.core.net.toUri

class PlantDetailsViewModel(
    private val application: Application,
    private val repository: PlantRepository,
    private val plantId: Int
) : ViewModel() {

    private val _plant = MutableStateFlow<Plant?>(null)
    val plant = _plant.asStateFlow()

    init {
        loadPlant()
    }

    private fun loadPlant() {
        viewModelScope.launch {
            repository.getPlantById(plantId).collect { p ->
                _plant.value = p
            }
        }
    }

    fun waterPlantNow() {
        toggleWateringStatus(System.currentTimeMillis())
    }

    fun toggleWateringStatus(dateMillis: Long) {
        val currentPlant = _plant.value ?: return
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val targetDay = sdf.format(Date(dateMillis))

        val currentHistory = currentPlant.wateringHistory.toMutableList()
        val existingEntry = currentHistory.find {
            sdf.format(Date(it)) == targetDay
        }

        if (existingEntry != null) {
            currentHistory.remove(existingEntry)
        } else {
            currentHistory.add(dateMillis)
        }

        currentHistory.sortDescending()
        val newLastWatered = currentHistory.firstOrNull() ?: currentPlant.lastWatered

        val updatedPlant = currentPlant.copy(
            lastWatered = newLastWatered,
            wateringHistory = currentHistory
        )

        viewModelScope.launch {
            repository.updatePlant(updatedPlant)
        }
    }

    fun deletePlant(onSuccess: () -> Unit) {
        val currentPlant = _plant.value ?: return
        viewModelScope.launch {
            repository.deletePlant(currentPlant)
            onSuccess()
        }
    }

    fun addTimelineEntry(title: String, photoUris: List<String>) {
        val currentPlant = _plant.value ?: return

        viewModelScope.launch {
            val savedUris = withContext(Dispatchers.IO) {
                photoUris.mapNotNull { uriString ->
                    copyImageToInternalStorage(uriString.toUri())
                }
            }

            val newEntry = TimelineEntry(
                title = title,
                date = System.currentTimeMillis(),
                photos = savedUris
            )

            val newTimeline = currentPlant.timeline.toMutableList().apply {
                add(newEntry)
                sortByDescending { it.date }
            }

            val updatedPlant = currentPlant.copy(timeline = newTimeline)
            repository.updatePlant(updatedPlant)
        }
    }

    fun deleteTimelineEntry(entry: TimelineEntry) {
        val currentPlant = _plant.value ?: return

        val newTimeline = currentPlant.timeline.toMutableList()
        newTimeline.remove(entry)

        val updatedPlant = currentPlant.copy(timeline = newTimeline)
        viewModelScope.launch {
            repository.updatePlant(updatedPlant)
        }
    }

    fun updateTimelineEntry(oldEntry: TimelineEntry, newTitle: String, newPhotos: List<String>) {
        val currentPlant = _plant.value ?: return

        viewModelScope.launch {
            val savedUris = withContext(Dispatchers.IO) {
                newPhotos.mapNotNull { uriString ->
                    if (uriString.startsWith("file://") || uriString.contains("plant_timeline_images")) {
                        uriString
                    } else {
                        copyImageToInternalStorage(uriString.toUri())
                    }
                }
            }

            val updatedEntry = oldEntry.copy(
                title = newTitle,
                photos = savedUris
            )

            val newTimeline = currentPlant.timeline.map {
                if (it == oldEntry) updatedEntry else it
            }

            val updatedPlant = currentPlant.copy(timeline = newTimeline)
            repository.updatePlant(updatedPlant)
        }
    }

    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val contentResolver = application.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null

            val directory = File(application.filesDir, "plant_timeline_images")
            if (!directory.exists()) { directory.mkdirs() }

            val fileName = "timeline_${UUID.randomUUID()}.jpg"
            val file = File(directory, fileName)

            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        fun provideFactory(plantId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PlantyApp)
                PlantDetailsViewModel(app, app.plantRepository, plantId)
            }
        }
    }
}