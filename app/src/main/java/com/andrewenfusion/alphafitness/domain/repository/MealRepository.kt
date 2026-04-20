package com.andrewenfusion.alphafitness.domain.repository

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun observeMeals(): Flow<List<MealEntry>>

    suspend fun lookupLocalInterpretation(
        description: String,
    ): AppResult<LogMealReviewState?>

    suspend fun interpretWithGateway(
        description: String,
    ): AppResult<LogMealReviewState>
}
