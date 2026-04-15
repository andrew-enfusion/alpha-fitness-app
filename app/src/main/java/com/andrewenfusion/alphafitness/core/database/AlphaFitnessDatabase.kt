package com.andrewenfusion.alphafitness.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andrewenfusion.alphafitness.core.database.dao.NutritionGuidanceDao
import com.andrewenfusion.alphafitness.core.database.dao.UserProfileDao
import com.andrewenfusion.alphafitness.core.database.entity.NutritionGuidanceEntity
import com.andrewenfusion.alphafitness.core.database.entity.UserProfileEntity

@Database(
    entities = [UserProfileEntity::class, NutritionGuidanceEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class AlphaFitnessDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    abstract fun nutritionGuidanceDao(): NutritionGuidanceDao

    companion object {
        const val DATABASE_NAME: String = "alpha_fitness.db"
    }
}
