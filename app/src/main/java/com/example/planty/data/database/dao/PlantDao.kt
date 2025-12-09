package com.example.planty.data.database.dao

import com.example.planty.data.database.entity.Plant
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Insert
    suspend fun insert(plant: Plant)

    @Update
    suspend fun update(plant: Plant)

    @Delete
    suspend fun delete(plant: Plant)

    @Query("SELECT * FROM Plant WHERE id = :plantId")
    fun getPlantById(plantId :Int): Flow<Plant>

    @Query("SELECT * FROM Plant ORDER BY name ASC")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM Plant")
    suspend fun getAllPlantsList(): List<Plant>

}