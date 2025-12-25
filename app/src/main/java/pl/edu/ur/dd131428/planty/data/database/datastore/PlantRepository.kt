package pl.edu.ur.dd131428.planty.data.database.datastore

import pl.edu.ur.dd131428.planty.data.database.dao.PlantDao
import pl.edu.ur.dd131428.planty.data.database.entity.Plant
import kotlinx.coroutines.flow.Flow

class PlantRepository(
    private val plantDao: PlantDao
) {

    fun getAllPlants(): Flow<List<Plant>> = plantDao.getAllPlants()

    fun getPlantById(plantId: Int): Flow<Plant> = plantDao.getPlantById(plantId)


    suspend fun addPlant(plant: Plant): Long {
        return plantDao.insert(plant)
    }

    suspend fun updatePlant(plant: Plant) {
        plantDao.update(plant)
    }

    suspend fun deletePlant(plant: Plant) {
        plantDao.delete(plant)
    }
}