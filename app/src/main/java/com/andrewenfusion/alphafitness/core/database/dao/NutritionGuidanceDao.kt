package com.andrewenfusion.alphafitness.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andrewenfusion.alphafitness.core.database.entity.NutritionGuidanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionGuidanceDao {
    @Query("SELECT * FROM nutrition_guidance WHERE userId = :userId LIMIT 1")
    fun observeByUserId(userId: String): Flow<NutritionGuidanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(guidance: NutritionGuidanceEntity)
}
