package com.andrewenfusion.alphafitness.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.andrewenfusion.alphafitness.core.database.entity.MealEntryEntity
import com.andrewenfusion.alphafitness.core.database.entity.MealItemEntity
import com.andrewenfusion.alphafitness.data.repository.model.MealEntryWithItems
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MealEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertMealEntry(mealEntry: MealEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertMealItems(mealItems: List<MealItemEntity>)

    @Query("SELECT * FROM meal_entry WHERE date = :date ORDER BY timestampEpochMillis ASC")
    protected abstract suspend fun getMealsForDateInternal(date: String): List<MealEntryEntity>

    @Query("SELECT * FROM meal_entry ORDER BY timestampEpochMillis DESC")
    abstract fun observeMeals(): Flow<List<MealEntryEntity>>

    @Transaction
    @Query("SELECT * FROM meal_entry ORDER BY timestampEpochMillis DESC LIMIT :limit")
    abstract suspend fun getRecentMealsWithItems(
        limit: Int,
    ): List<MealEntryWithItems>

    @Transaction
    open suspend fun saveMealAndLoadMealsForDate(
        mealEntry: MealEntryEntity,
        mealItems: List<MealItemEntity>,
    ): List<MealEntryEntity> {
        upsertMealEntry(mealEntry)
        if (mealItems.isNotEmpty()) {
            upsertMealItems(mealItems)
        }
        return getMealsForDateInternal(mealEntry.date)
    }

    suspend fun getMealsForDate(
        date: String,
    ): List<MealEntryEntity> = getMealsForDateInternal(date)
}
