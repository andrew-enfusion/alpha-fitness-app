package com.andrewenfusion.alphafitness.data.repository

import com.andrewenfusion.alphafitness.core.common.dispatcher.AppDispatchers
import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.database.dao.MealEntryDao
import com.andrewenfusion.alphafitness.data.gateway.log.LogInterpretationGateway
import com.andrewenfusion.alphafitness.data.mapper.toDomain
import com.andrewenfusion.alphafitness.data.mapper.toEntity
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import com.andrewenfusion.alphafitness.domain.model.MealItem
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.model.SavedMealMemory
import com.andrewenfusion.alphafitness.domain.repository.MealRepository
import com.andrewenfusion.alphafitness.data.mapper.toSavedMealMemory
import javax.inject.Inject
import javax.inject.Singleton
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class RoomMealRepository @Inject constructor(
    private val mealEntryDao: MealEntryDao,
    private val logInterpretationGateway: LogInterpretationGateway,
    private val dispatchers: AppDispatchers,
) : MealRepository {
    override fun observeMeals(): Flow<List<MealEntry>> =
        mealEntryDao
            .observeMeals()
            .map { entities -> entities.map { entity -> entity.toDomain() } }
            .flowOn(dispatchers.io)

    override suspend fun saveMealAndLoadMealsForDate(
        mealEntry: MealEntry,
        mealItems: List<MealItem>,
    ): AppResult<List<MealEntry>> =
        withContext(dispatchers.io) {
            runCatching {
                mealEntryDao.saveMealAndLoadMealsForDate(
                    mealEntry = mealEntry.toEntity(),
                    mealItems = mealItems.map { item -> item.toEntity() },
                ).map { entity -> entity.toDomain() }
            }.fold(
                onSuccess = { meals -> AppResult.Success(meals) },
                onFailure = { throwable ->
                    AppResult.Failure(
                        AppError.Storage(
                            message = throwable.message ?: "Failed to store the reviewed meal.",
                        ),
                    )
                },
            )
        }

    override suspend fun getMealsForDate(
        date: LocalDate,
    ): AppResult<List<MealEntry>> =
        withContext(dispatchers.io) {
            runCatching {
                mealEntryDao
                    .getMealsForDate(date.toString())
                    .map { entity -> entity.toDomain() }
            }.fold(
                onSuccess = { meals -> AppResult.Success(meals) },
                onFailure = { throwable ->
                    AppResult.Failure(
                        AppError.Storage(
                            message = throwable.message ?: "Failed to load meals for the selected day.",
                        ),
                    )
                },
            )
        }

    override suspend fun getRecentSavedMeals(
        limit: Int,
    ): AppResult<List<SavedMealMemory>> =
        withContext(dispatchers.io) {
            runCatching {
                mealEntryDao
                    .getRecentMealsWithItems(limit)
                    .map { mealWithItems -> mealWithItems.toSavedMealMemory() }
            }.fold(
                onSuccess = { meals -> AppResult.Success(meals) },
                onFailure = { throwable ->
                    AppResult.Failure(
                        AppError.Storage(
                            message = throwable.message ?: "Failed to load recent saved meals.",
                        ),
                    )
                },
            )
        }

    override suspend fun interpretWithGateway(
        description: String,
    ): AppResult<LogMealReviewState> = logInterpretationGateway.interpretMealDescription(description)
}
