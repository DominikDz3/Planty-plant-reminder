package pl.edu.ur.dd131428.planty.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pl.edu.ur.dd131428.planty.data.database.entity.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plant: Plant): Long

    @Update
    suspend fun update(plant: Plant)

    @Delete
    suspend fun delete(plant: Plant)

    @Query("SELECT * FROM plant WHERE id = :plantId")
    fun getPlantById(plantId: Int): Flow<Plant>

    @Query("SELECT * FROM plant ORDER BY name ASC")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plant")
    suspend fun getAllPlantsList(): List<Plant>
}