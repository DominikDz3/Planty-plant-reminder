package com.example.planty.data.database.datastore

import com.example.planty.data.database.dao.PlantDao
import com.example.planty.data.database.entity.Plant
import kotlinx.coroutines.flow.Flow

class PlantRepository(
    private val plantDao: PlantDao,
) {
    // PlantDao

    fun getAllPlants(): Flow<List<Plant>> = plantDao.getAllPlants()

    fun getPlantById(plantId: Int): Flow<Plant> = plantDao.getPlantById(plantId)

    suspend fun addPlant(plant: Plant) {
        plantDao.insert(plant)
    }

    suspend fun updatePlant(plant: Plant) {
        plantDao.update(plant)
    }

    suspend fun deletePlant(plant: Plant) {
        plantDao.delete(plant)
    }
}