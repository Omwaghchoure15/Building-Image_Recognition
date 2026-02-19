package com.example.buildingimagerecognition.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BuildingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuilding(building: BuildingEntity)

    @Query("SELECT * FROM buildings")
    suspend fun getAllBuildings(): List<BuildingEntity>

    @Query("DELETE FROM buildings")
    suspend fun deleteAllBuildings()

    @Delete
    suspend fun deleteBuilding(building: BuildingEntity)
}
