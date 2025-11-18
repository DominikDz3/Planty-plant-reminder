package com.example.planty.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "watering_history",
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class WaterHistory (
    @PrimaryKey(autoGenerate = true)

    val id: Int = 0,
    val plantId: Int,
    val wateredDate: Long

)