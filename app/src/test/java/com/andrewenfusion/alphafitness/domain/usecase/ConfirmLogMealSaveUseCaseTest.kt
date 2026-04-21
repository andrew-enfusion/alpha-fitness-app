package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.common.time.TimeProvider
import com.andrewenfusion.alphafitness.domain.engine.DailyMetricsCalculator
import com.andrewenfusion.alphafitness.domain.model.DailyMetrics
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewItem
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import com.andrewenfusion.alphafitness.domain.model.MealItem
import com.andrewenfusion.alphafitness.domain.model.MealSourceType
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidance
import com.andrewenfusion.alphafitness.domain.model.Sex
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.domain.repository.DailyMetricsRepository
import com.andrewenfusion.alphafitness.domain.repository.MealRepository
import com.andrewenfusion.alphafitness.domain.repository.NutritionGuidanceRepository
import com.andrewenfusion.alphafitness.domain.repository.UserProfileRepository
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfirmLogMealSaveUseCaseTest {
    @Test
    fun savesMealAndRecomputesDailyMetricsFromScratch() = runBlocking {
        val fixedNow = Instant.parse("2026-04-21T12:00:00Z")
        val mealRepository = FakeMealRepository(
            existingMealsForDate = listOf(
                MealEntry(
                    id = "existing-meal",
                    userId = UserProfile.LOCAL_USER_ID,
                    date = LocalDate.of(2026, 4, 21),
                    timestamp = Instant.parse("2026-04-21T08:00:00Z"),
                    rawInput = "Greek yogurt",
                    sourceType = MealSourceType.TEXT,
                    photoUri = null,
                    totalCalories = 180,
                    totalProtein = 16f,
                    totalCarbs = 18f,
                    totalFat = 4f,
                    confidence = 0.9f,
                    isUserEdited = false,
                    reviewRequired = true,
                ),
            ),
        )
        val metricsRepository = FakeDailyMetricsRepository()
        val useCase = ConfirmLogMealSaveUseCase(
            mealRepository = mealRepository,
            dailyMetricsRepository = metricsRepository,
            nutritionGuidanceRepository = FakeNutritionGuidanceRepository(
                guidance = NutritionGuidance(
                    userId = UserProfile.LOCAL_USER_ID,
                    calorieTarget = 2200,
                    suggestedProteinRange = "120-150g",
                    suggestedCarbRange = "220-260g",
                    suggestedFatRange = "60-75g",
                    derivationExplanation = "Working target",
                    notes = "",
                    generatedAt = fixedNow,
                ),
            ),
            userProfileRepository = FakeUserProfileRepository(),
            dailyMetricsCalculator = DailyMetricsCalculator(),
            timeProvider = object : TimeProvider {
                override fun now(): Instant = fixedNow
            },
        )

        val result = useCase(sampleReviewState())

        assertTrue(result is AppResult.Success)
        val outcome = (result as AppResult.Success).value
        assertTrue(outcome is ConfirmLogMealSaveOutcome.Success)
        assertEquals(2, metricsRepository.lastSavedMetrics?.mealCount)
        assertEquals(700, metricsRepository.lastSavedMetrics?.totalCalories)
        assertEquals(2200, metricsRepository.lastSavedMetrics?.targetCalories)
    }

    private fun sampleReviewState(): LogMealReviewState =
        LogMealReviewState(
            submittedText = "Chicken shawarma wrap",
            interpretationSource = LogMealInterpretationSource.AI_FALLBACK,
            items = listOf(
                LogMealReviewItem(
                    displayName = "Chicken shawarma wrap",
                    portionDescription = "1 serving",
                    calories = 520,
                    protein = 28f,
                    carbs = 46f,
                    fat = 20f,
                    assumptions = "Used a default serving estimate.",
                    confidence = 0.72f,
                ),
            ),
            assumptions = listOf("Used a default serving estimate."),
            requiresReview = true,
            confidence = 0.72f,
        )

    private class FakeMealRepository(
        private val existingMealsForDate: List<MealEntry> = emptyList(),
    ) : MealRepository {
        override fun observeMeals(): Flow<List<MealEntry>> = flowOf(existingMealsForDate)

        override suspend fun saveMealAndLoadMealsForDate(
            mealEntry: MealEntry,
            mealItems: List<MealItem>,
        ): AppResult<List<MealEntry>> = AppResult.Success(existingMealsForDate + mealEntry)

        override suspend fun getMealsForDate(
            date: LocalDate,
        ): AppResult<List<MealEntry>> = AppResult.Success(existingMealsForDate)

        override suspend fun lookupLocalInterpretation(
            description: String,
        ): AppResult<LogMealReviewState?> = AppResult.Success(null)

        override suspend fun interpretWithGateway(
            description: String,
        ): AppResult<LogMealReviewState> = AppResult.Failure(
            AppError.Unsupported("Not used in this test."),
        )
    }

    private class FakeDailyMetricsRepository : DailyMetricsRepository {
        var lastSavedMetrics: DailyMetrics? = null

        override suspend fun replaceDailyMetrics(metrics: DailyMetrics): AppResult<Unit> {
            lastSavedMetrics = metrics
            return AppResult.Success(Unit)
        }
    }

    private class FakeNutritionGuidanceRepository(
        private val guidance: NutritionGuidance?,
    ) : NutritionGuidanceRepository {
        override suspend fun getNutritionGuidance(userId: String): NutritionGuidance? = guidance

        override fun observeNutritionGuidance(userId: String): Flow<NutritionGuidance?> =
            flowOf(guidance)

        override suspend fun refreshNutritionGuidance(profile: UserProfile): AppResult<NutritionGuidance> =
            AppResult.Failure(AppError.Unsupported("Not used in this test."))

        override suspend fun resetWorkingTargetToBaseline(profile: UserProfile): AppResult<NutritionGuidance> =
            AppResult.Failure(AppError.Unsupported("Not used in this test."))
    }

    private class FakeUserProfileRepository : UserProfileRepository {
        private val profile = UserProfile(
            age = 30,
            sex = Sex.MALE,
            heightCm = 178f,
            weightKg = 82f,
            exerciseLevel = ExerciseLevel.MODERATE,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
            calorieTarget = 2100,
            createdAt = Instant.parse("2026-04-20T12:00:00Z"),
            updatedAt = Instant.parse("2026-04-20T12:00:00Z"),
        )

        override suspend fun getUserProfile(userId: String): UserProfile = profile

        override fun observeUserProfile(userId: String): Flow<UserProfile?> = flowOf(profile)

        override suspend fun upsertUserProfile(profile: UserProfile): AppResult<Unit> =
            AppResult.Failure(AppError.Unsupported("Not used in this test."))
    }
}
