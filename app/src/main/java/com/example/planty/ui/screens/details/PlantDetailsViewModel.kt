package com.example.planty.ui.screens.details

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlantDetailsViewModel(
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

    companion object {
        fun provideFactory(plantId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PlantyApp)
                PlantDetailsViewModel(app.plantRepository, plantId)
            }
        }
    }
}