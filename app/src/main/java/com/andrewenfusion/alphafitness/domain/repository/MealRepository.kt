package com.andrewenfusion.alphafitness.domain.repository

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import com.andrewenfusion.alphafitness.domain.model.MealItem
import com.andrewenfusion.alphafitness.domain.model.SavedMealMemory
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun observeMeals(): Flow<List<MealEntry>>

    suspend fun saveMealAndLoadMealsForDate(
        mealEntry: MealEntry,
        mealItems: List<MealItem>,
    ): AppResult<List<MealEntry>>

    suspend fun getMealsForDate(
        date: LocalDate,
    ): AppResult<List<MealEntry>>

    suspend fun getRecentSavedMeals(
        limit: Int,
    ): AppResult<List<SavedMealMemory>>

    suspend fun interpretWithGateway(
        description: String,
    ): AppResult<LogMealReviewState>
}
