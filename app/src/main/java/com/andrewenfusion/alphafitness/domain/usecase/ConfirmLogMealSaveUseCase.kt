package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.common.time.TimeProvider
import com.andrewenfusion.alphafitness.domain.engine.DailyMetricsCalculator
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import com.andrewenfusion.alphafitness.domain.model.MealItem
import com.andrewenfusion.alphafitness.domain.model.MealSourceType
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.domain.repository.DailyMetricsRepository
import com.andrewenfusion.alphafitness.domain.repository.MealRepository
import com.andrewenfusion.alphafitness.domain.repository.NutritionGuidanceRepository
import com.andrewenfusion.alphafitness.domain.repository.UserProfileRepository
import java.time.Instant
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

class ConfirmLogMealSaveUseCase @Inject constructor(
    private val mealRepository: MealRepository,
    private val dailyMetricsRepository: DailyMetricsRepository,
    private val nutritionGuidanceRepository: NutritionGuidanceRepository,
    private val userProfileRepository: UserProfileRepository,
    private val dailyMetricsCalculator: DailyMetricsCalculator,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(
        reviewState: LogMealReviewState,
    ): AppResult<ConfirmLogMealSaveOutcome> {
        if (reviewState.items.isEmpty()) {
            return AppResult.Failure(
                AppError.Validation(
                    message = "Review a parsed meal before saving it.",
                ),
            )
        }

        val timestamp = timeProvider.now()
        val date = timestamp.toLocalDate()
        val targetCalories = resolveTargetCalories()
            ?: return AppResult.Failure(
                AppError.Storage(
                    message = "Couldn't determine the current calorie target for daily totals.",
                ),
            )

        val mealEntry = buildMealEntry(
            reviewState = reviewState,
            timestamp = timestamp,
            date = date,
        )
        val mealItems = buildMealItems(
            reviewState = reviewState,
            mealEntryId = mealEntry.id,
        )

        return when (val saveResult = mealRepository.saveMealAndLoadMealsForDate(mealEntry, mealItems)) {
            is AppResult.Success -> {
                val recomputedMetrics = dailyMetricsCalculator.calculate(
                    date = date,
                    meals = saveResult.value,
                    targetCalories = targetCalories,
                    recomputedAt = timestamp,
                )

                when (val metricsResult = dailyMetricsRepository.replaceDailyMetrics(recomputedMetrics)) {
                    is AppResult.Success -> {
                        AppResult.Success(
                            ConfirmLogMealSaveOutcome.Success(
                                savedMealId = mealEntry.id,
                            ),
                        )
                    }
                    is AppResult.Failure -> {
                        AppResult.Success(
                            ConfirmLogMealSaveOutcome.MetricsRefreshFailed(
                                savedMealId = mealEntry.id,
                                message = metricsResult.error.message,
                            ),
                        )
                    }
                }
            }
            is AppResult.Failure -> saveResult
        }
    }

    private suspend fun resolveTargetCalories(): Int? {
        val userId = UserProfile.LOCAL_USER_ID
        val guidanceTarget = nutritionGuidanceRepository
            .getNutritionGuidance(userId)
            ?.calorieTarget

        if (guidanceTarget != null && guidanceTarget > 0) {
            return guidanceTarget
        }

        return userProfileRepository
            .getUserProfile(userId)
            ?.calorieTarget
            ?.takeIf { it > 0 }
    }

    private fun buildMealEntry(
        reviewState: LogMealReviewState,
        timestamp: Instant,
        date: java.time.LocalDate,
    ): MealEntry =
        MealEntry(
            id = UUID.randomUUID().toString(),
            userId = UserProfile.LOCAL_USER_ID,
            date = date,
            timestamp = timestamp,
            rawInput = reviewState.submittedText,
            sourceType = MealSourceType.TEXT,
            photoUri = null,
            totalCalories = reviewState.totalCalories,
            totalProtein = reviewState.totalProtein,
            totalCarbs = reviewState.totalCarbs,
            totalFat = reviewState.totalFat,
            confidence = reviewState.confidence,
            isUserEdited = false,
            reviewRequired = reviewState.requiresReview,
        )

    private fun buildMealItems(
        reviewState: LogMealReviewState,
        mealEntryId: String,
    ): List<MealItem> =
        reviewState.items.map { item ->
            MealItem(
                id = UUID.randomUUID().toString(),
                mealEntryId = mealEntryId,
                foodReferenceId = null,
                displayName = item.displayName,
                quantity = null,
                unit = null,
                portionDescription = item.portionDescription,
                calories = item.calories,
                protein = item.protein,
                carbs = item.carbs,
                fat = item.fat,
                assumptions = item.assumptions.ifBlank {
                    reviewState.assumptions.joinToString(separator = " | ")
                },
                confidence = item.confidence,
            )
        }

    private fun Instant.toLocalDate(): java.time.LocalDate =
        atZone(ZoneId.systemDefault()).toLocalDate()
}

sealed interface ConfirmLogMealSaveOutcome {
    data class Success(
        val savedMealId: String,
    ) : ConfirmLogMealSaveOutcome

    data class MetricsRefreshFailed(
        val savedMealId: String,
        val message: String,
    ) : ConfirmLogMealSaveOutcome
}
