package com.example.planty.data.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DateConverter {
    // --- Konwertery dla Historii (List<Long>) ---
    @TypeConverter
    fun fromLongList(value: String?): List<Long> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<Long>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun toLongList(list: List<Long>?): String {
        return Gson().toJson(list ?: emptyList<Long>())
    }

    // --- NOWE: Konwertery dla Zdjęć (List<String>) ---
    // To naprawi błąd w Plant.kt
    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return Gson().toJson(list ?: emptyList<String>())
    }
}