package com.example.planty.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.planty.data.database.converters.DateConverter

@Entity(tableName = "plant")
@TypeConverters(DateConverter::class)
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val wateringFrequencyDays: Int,
    val lastWatered: Long,
    val photoUris: List<String> = emptyList(),

    val wateringHistory: List<Long> = emptyList()
)