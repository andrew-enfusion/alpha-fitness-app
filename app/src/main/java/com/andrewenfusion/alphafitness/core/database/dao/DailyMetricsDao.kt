package com.andrewenfusion.alphafitness.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.andrewenfusion.alphafitness.core.database.entity.DailyMetricsEntity

@Dao
interface DailyMetricsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(metrics: DailyMetricsEntity)
}
