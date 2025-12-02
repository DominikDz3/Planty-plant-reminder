package com.example.planty.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Plant (
    @PrimaryKey(autoGenerate = true)

    val id: Int,
    val name: String,
    val description: String,
    val photoUri: String?,
    val wateringFrequencyDays: Int,
    val lastWatered: Long
)