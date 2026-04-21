package com.andrewenfusion.alphafitness.di

import android.content.Context
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.andrewenfusion.alphafitness.core.database.AlphaFitnessDatabase
import com.andrewenfusion.alphafitness.core.database.dao.DailyMetricsDao
import com.andrewenfusion.alphafitness.core.database.dao.MealEntryDao
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
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

    @Provides
    fun provideUserProfileDao(database: AlphaFitnessDatabase): UserProfileDao =
        database.userProfileDao()

    @Provides
    fun provideNutritionGuidanceDao(database: AlphaFitnessDatabase): NutritionGuidanceDao =
        database.nutritionGuidanceDao()

    @Provides
    fun provideMealEntryDao(database: AlphaFitnessDatabase): MealEntryDao =
        database.mealEntryDao()

    @Provides
    fun provideDailyMetricsDao(database: AlphaFitnessDatabase): DailyMetricsDao =
        database.dailyMetricsDao()

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

    val MIGRATION_2_3: Migration =
        object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `meal_entry` (
                        `id` TEXT NOT NULL,
                        `userId` TEXT NOT NULL,
                        `date` TEXT NOT NULL,
                        `timestampEpochMillis` INTEGER NOT NULL,
                        `rawInput` TEXT,
                        `sourceType` TEXT NOT NULL,
                        `photoUri` TEXT,
                        `totalCalories` INTEGER NOT NULL,
                        `totalProtein` REAL NOT NULL,
                        `totalCarbs` REAL NOT NULL,
                        `totalFat` REAL NOT NULL,
                        `confidence` REAL NOT NULL,
                        `isUserEdited` INTEGER NOT NULL,
                        `reviewRequired` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `meal_item` (
                        `id` TEXT NOT NULL,
                        `mealEntryId` TEXT NOT NULL,
                        `foodReferenceId` TEXT,
                        `displayName` TEXT NOT NULL,
                        `quantity` REAL,
                        `unit` TEXT,
                        `portionDescription` TEXT,
                        `calories` INTEGER NOT NULL,
                        `protein` REAL NOT NULL,
                        `carbs` REAL NOT NULL,
                        `fat` REAL NOT NULL,
                        `assumptions` TEXT NOT NULL,
                        `confidence` REAL NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`mealEntryId`) REFERENCES `meal_entry`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_meal_item_mealEntryId`
                    ON `meal_item` (`mealEntryId`)
                    """.trimIndent(),
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_metrics` (
                        `date` TEXT NOT NULL,
                        `totalCalories` INTEGER NOT NULL,
                        `totalProtein` REAL NOT NULL,
                        `totalCarbs` REAL NOT NULL,
                        `totalFat` REAL NOT NULL,
                        `targetCalories` INTEGER NOT NULL,
                        `mealCount` INTEGER NOT NULL,
                        `lastRecomputedAtEpochMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`date`)
                    )
                    """.trimIndent(),
                )
            }
        }
}
