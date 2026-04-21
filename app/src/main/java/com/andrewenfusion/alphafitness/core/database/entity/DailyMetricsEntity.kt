package com.andrewenfusion.alphafitness.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_metrics")
data class DailyMetricsEntity(
    @PrimaryKey val date: String,
    val totalCalories: Int,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val targetCalories: Int,
    val mealCount: Int,
    val lastRecomputedAtEpochMillis: Long,
)
