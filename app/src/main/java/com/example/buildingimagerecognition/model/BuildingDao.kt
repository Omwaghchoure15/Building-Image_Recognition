package com.example.buildingimagerecognition.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.buildingimagerecognition.data.BuildingEntity

@Dao
interface BuildingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuilding(building: BuildingEntity)

    @Query("SELECT * FROM BuildingEntity")
    suspend fun getAllBuildings(): List<BuildingEntity>
}

