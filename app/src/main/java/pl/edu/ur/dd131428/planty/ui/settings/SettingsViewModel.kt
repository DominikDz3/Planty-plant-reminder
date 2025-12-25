package pl.edu.ur.dd131428.planty.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import pl.edu.ur.dd131428.planty.PlantyApp
import pl.edu.ur.dd131428.planty.data.database.datastore.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<String> = settingsRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "SYSTEM"
        )

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            settingsRepository.saveThemeMode(mode)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PlantyApp)
                SettingsViewModel(application.settingsRepository)
            }
        }
    }
}