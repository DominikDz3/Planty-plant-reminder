package com.example.planty.ui.screens.add_edit

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.planty.PlantyApp
import com.example.planty.data.database.datastore.PlantRepository
import com.example.planty.data.database.entity.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.core.net.toUri

class AddEditViewModel(
    private val application: Application,
    private val repository: PlantRepository
) : ViewModel() {

    private val _plantName = MutableStateFlow("")
    val plantName = _plantName.asStateFlow()

    private val _plantDescription = MutableStateFlow("")
    val plantDescription = _plantDescription.asStateFlow()

    private val _wateringFreq = MutableStateFlow("")
    val wateringFreq = _wateringFreq.asStateFlow()

    private val _photoUris = MutableStateFlow<List<String>>(emptyList())
    val photoUris = _photoUris.asStateFlow()

    private val _lastWateredDate = MutableStateFlow(System.currentTimeMillis())
    val lastWateredDate = _lastWateredDate.asStateFlow()

    private var tempCameraUri: Uri? = null

    fun onNameChange(newName: String) {
        _plantName.value = newName
    }

    fun onDescriptionChange(newDesc: String) {
        _plantDescription.value = newDesc
    }

    fun onFrequencyChange(newFreq: String) {
        if (newFreq.all { it.isDigit() }) {
            _wateringFreq.value = newFreq
        }
    }

    fun onDateChange(newDate: Long) {
        _lastWateredDate.value = newDate
    }

    fun onPhotosSelected(uris: List<String>) {
        viewModelScope.launch {
            val copiedUris = withContext(Dispatchers.IO) {
                uris.mapNotNull { uriString ->
                    copyImageToInternalStorage(uriString.toUri())
                }
            }
            _photoUris.value = _photoUris.value + copiedUris
        }
    }

    fun createUriForCamera(): Uri {
        val directory = File(application.filesDir, "plant_images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "img_cam_${UUID.randomUUID()}.jpg")

        val authority = "${application.packageName}.provider"
        val uri = FileProvider.getUriForFile(application, authority, file)

        tempCameraUri = uri
        return uri
    }

    fun onPhotoTaken(success: Boolean) {
        if (success && tempCameraUri != null) {
            _photoUris.value = _photoUris.value + tempCameraUri.toString()
        }
        tempCameraUri = null
    }

    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val contentResolver = application.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null

            val directory = File(application.filesDir, "plant_images")
            if (!directory.exists()) { directory.mkdirs() }

            val fileName = "img_gallery_${UUID.randomUUID()}.jpg"
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

    fun removePhoto(uri: String) {
        _photoUris.value = _photoUris.value.filter { it != uri }
    }

    fun savePlant(onSuccess: () -> Unit) {
        val name = _plantName.value
        val freq = _wateringFreq.value.toIntOrNull()
        val description = _plantDescription.value
        val lastWatered = _lastWateredDate.value

        if (name.isBlank() || freq == null) {
            return
        }

        viewModelScope.launch {
            val newPlant = Plant(
                id = 0,
                name = name,
                description = description,
                wateringFrequencyDays = freq,
                photoUris = _photoUris.value,
                lastWatered = lastWatered
            )
            repository.addPlant(newPlant)
            onSuccess()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PlantyApp)
                AddEditViewModel(
                    application = application,
                    repository = application.plantRepository
                )
            }
        }
    }
}