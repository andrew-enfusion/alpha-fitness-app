package com.andrewenfusion.alphafitness.data.repository

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.data.gateway.log.LogInterpretationGateway
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import com.andrewenfusion.alphafitness.domain.repository.MealRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Singleton
class RoomMealRepository @Inject constructor(
    private val logInterpretationGateway: LogInterpretationGateway,
) : MealRepository {
    override fun observeMeals(): Flow<List<MealEntry>> = flowOf(emptyList())

    override suspend fun lookupLocalInterpretation(
        description: String,
    ): AppResult<LogMealReviewState?> = AppResult.Success(null)

    override suspend fun interpretWithGateway(
        description: String,
    ): AppResult<LogMealReviewState> = logInterpretationGateway.interpretMealDescription(description)
}
