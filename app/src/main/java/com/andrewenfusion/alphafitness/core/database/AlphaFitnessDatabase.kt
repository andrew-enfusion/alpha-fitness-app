package com.andrewenfusion.alphafitness.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andrewenfusion.alphafitness.core.database.dao.UserProfileDao
import com.andrewenfusion.alphafitness.core.database.entity.UserProfileEntity

@Database(
    entities = [UserProfileEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AlphaFitnessDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        const val DATABASE_NAME: String = "alpha_fitness.db"
    }
}
