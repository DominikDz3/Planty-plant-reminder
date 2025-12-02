package com.example.planty.data.database.dao

import com.example.planty.data.database.entity.WaterHistory
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterHistoryDao {
    @Insert
    fun insertHistory(history: WaterHistory)

    @Query("SELECT * FROM watering_history WHERE plantId = :plantId ORDER BY wateredDate DESC")
    fun getWaterHistoryForPlant(plantId: Int): Flow<List<WaterHistory>>

    @Query("SELECT * FROM watering_history")
    fun getAllHistory(): Flow<List<WaterHistory>>
}

