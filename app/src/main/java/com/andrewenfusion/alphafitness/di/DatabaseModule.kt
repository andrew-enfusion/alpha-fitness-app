package com.andrewenfusion.alphafitness.di

import android.content.Context
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.andrewenfusion.alphafitness.core.database.AlphaFitnessDatabase
import com.andrewenfusion.alphafitness.core.database.dao.NutritionGuidanceDao
import com.andrewenfusion.alphafitness.core.database.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAlphaFitnessDatabase(
        @ApplicationContext context: Context,
    ): AlphaFitnessDatabase =
        Room.databaseBuilder(
            context,
            AlphaFitnessDatabase::class.java,
            AlphaFitnessDatabase.DATABASE_NAME,
        ).addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideUserProfileDao(database: AlphaFitnessDatabase): UserProfileDao =
        database.userProfileDao()

    @Provides
    fun provideNutritionGuidanceDao(database: AlphaFitnessDatabase): NutritionGuidanceDao =
        database.nutritionGuidanceDao()

    val MIGRATION_1_2: Migration =
        object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `nutrition_guidance` (
                        `userId` TEXT NOT NULL,
                        `calorieTarget` INTEGER NOT NULL,
                        `suggestedProteinRange` TEXT NOT NULL,
                        `suggestedCarbRange` TEXT NOT NULL,
                        `suggestedFatRange` TEXT NOT NULL,
                        `derivationExplanation` TEXT NOT NULL,
                        `notes` TEXT NOT NULL,
                        `generatedAtEpochMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`userId`)
                    )
                    """.trimIndent(),
                )
            }
        }
}
