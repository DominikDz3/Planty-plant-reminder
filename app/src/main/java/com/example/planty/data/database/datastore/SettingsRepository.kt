package com.example.planty.data.database.datastore

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.core.content.edit

class SettingsRepository(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("planty_settings", Context.MODE_PRIVATE)

   companion object {
       private const val THEME_MODE_KEY = "theme_mode"
       private const val DYNAMIC_COLOR_KEY = "dynamic_color"
   }

    private val _themeMode = MutableStateFlow(sharedPreferences.getString(THEME_MODE_KEY, "SYSTEM") ?: "System")
    private val _isDynamicColor = MutableStateFlow(sharedPreferences.getBoolean(DYNAMIC_COLOR_KEY, true))

    val themeMode: Flow<String> = _themeMode.asStateFlow()
    val isDynamicColor: Flow<Boolean> = _isDynamicColor.asStateFlow()

    fun saveThemeMode(mode: String) {
        sharedPreferences.edit { putString(THEME_MODE_KEY, mode) }
        _themeMode.value = mode
    }

    fun saveDynamicColor(isDynamic: Boolean) {
        sharedPreferences.edit { putBoolean(DYNAMIC_COLOR_KEY, isDynamic) }
        _isDynamicColor.value = isDynamic
    }
}