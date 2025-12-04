package com.example.planty.data.database.datastore

import com.example.planty.data.database.dao.PlantDao
import com.example.planty.data.database.dao.WaterHistoryDao
import com.example.planty.data.database.entity.Plant
import com.example.planty.data.database.entity.WaterHistory
import kotlinx.coroutines.flow.Flow

class PlantRepository(
    private val plantDao: PlantDao,
    private val waterHistoryDao: WaterHistoryDao
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

    // WaterHistoryDao

    fun getAllHistory(): Flow<List<WaterHistory>> = waterHistoryDao.getAllHistory()

    fun getWaterHistoryForPlant(plantId: Int): Flow<List<WaterHistory>> {
        return waterHistoryDao.getWaterHistoryForPlant(plantId)
    }

    suspend fun addWaterHistory(plantId: Int, wateredDate: Long) {
        val historyEntry = WaterHistory(plantId = plantId, wateredDate = wateredDate)
        waterHistoryDao.insertHistory(historyEntry)
    }






}