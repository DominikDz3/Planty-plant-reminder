package com.example.planty.data.database.converters

import androidx.room.TypeConverter
import com.example.planty.data.database.entity.TimelineEntry
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

    // --- Konwertery dla Zdjęć (List<String>) ---
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

    // --- NOWE: Konwertery dla Osi Czasu (List<TimelineEntry>) ---
    @TypeConverter
    fun fromTimelineList(value: String?): List<TimelineEntry> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<TimelineEntry>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun toTimelineList(list: List<TimelineEntry>?): String {
        return Gson().toJson(list ?: emptyList<TimelineEntry>())
    }
}