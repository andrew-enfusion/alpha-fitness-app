package com.andrewenfusion.alphafitness.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andrewenfusion.alphafitness.core.database.dao.DailyMetricsDao
import com.andrewenfusion.alphafitness.core.database.dao.MealEntryDao
import com.andrewenfusion.alphafitness.core.database.dao.NutritionGuidanceDao
import com.andrewenfusion.alphafitness.core.database.dao.UserProfileDao
import com.andrewenfusion.alphafitness.core.database.entity.DailyMetricsEntity
import com.andrewenfusion.alphafitness.core.database.entity.MealEntryEntity
import com.andrewenfusion.alphafitness.core.database.entity.MealItemEntity
import com.andrewenfusion.alphafitness.core.database.entity.NutritionGuidanceEntity
import com.andrewenfusion.alphafitness.core.database.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        NutritionGuidanceEntity::class,
        MealEntryEntity::class,
        MealItemEntity::class,
        DailyMetricsEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
abstract class AlphaFitnessDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    abstract fun nutritionGuidanceDao(): NutritionGuidanceDao

    abstract fun mealEntryDao(): MealEntryDao

    abstract fun dailyMetricsDao(): DailyMetricsDao

    companion object {
        const val DATABASE_NAME: String = "alpha_fitness.db"
    }
}
