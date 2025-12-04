package com.example.planty.ui.screens.add_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.planty.PlantyApp
import com.example.planty.data.database.datastore.PlantRepository
import com.example.planty.data.database.entity.Plant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditViewModel(
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

    fun onPhotosSelected(uris: List<String>) {
        _photoUris.value = _photoUris.value + uris
    }

    fun removePhoto(uri: String) {
        _photoUris.value = _photoUris.value.filter { it != uri }
    }

    fun savePlant(onSuccess: () -> Unit) {
        val name = _plantName.value
        val freq = _wateringFreq.value.toIntOrNull()
        val description = _plantDescription.value

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
                lastWatered = System.currentTimeMillis()
            )
            repository.addPlant(newPlant)
            onSuccess()
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PlantyApp)
                AddEditViewModel(application.plantRepository)
            }
        }
    }
}