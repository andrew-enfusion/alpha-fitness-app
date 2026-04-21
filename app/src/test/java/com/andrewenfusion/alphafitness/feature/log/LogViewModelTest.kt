package com.andrewenfusion.alphafitness.feature.log

import com.andrewenfusion.alphafitness.core.common.dispatcher.AppDispatchers
import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewItem
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.model.MealItem
import com.andrewenfusion.alphafitness.domain.repository.MealRepository
import com.andrewenfusion.alphafitness.domain.repository.DailyMetricsRepository
import com.andrewenfusion.alphafitness.domain.repository.NutritionGuidanceRepository
import com.andrewenfusion.alphafitness.domain.repository.UserProfileRepository
import com.andrewenfusion.alphafitness.domain.model.DailyMetrics
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidance
import com.andrewenfusion.alphafitness.domain.model.Sex
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.domain.usecase.ConfirmLogMealSaveUseCase
import com.andrewenfusion.alphafitness.domain.usecase.InterpretLogMealUseCase
import com.andrewenfusion.alphafitness.domain.usecase.PrepareLogComposerSubmissionUseCase
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import com.andrewenfusion.alphafitness.domain.engine.DailyMetricsCalculator
import com.andrewenfusion.alphafitness.core.common.time.TimeProvider
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LogViewModelTest {
    private val dispatchers = AppDispatchers(
        io = Dispatchers.Unconfined,
        default = Dispatchers.Unconfined,
        main = Dispatchers.Unconfined,
    )

    @Test
    fun submitWithBlankDraftShowsValidationState() {
        val viewModel = createViewModel()

        viewModel.onEvent(LogUiEvent.SubmitClicked)

        assertEquals(
            LogOutputState.ValidationError(
                message = "Enter a meal description first.",
            ),
            viewModel.uiState.value.outputState,
        )
    }

    @Test
    fun submitWithDraftBuildsReviewReadyState() {
        val viewModel = createViewModel()

        viewModel.onEvent(LogUiEvent.DraftChanged("Chicken shawarma wrap"))
        viewModel.onEvent(LogUiEvent.SubmitClicked)

        assertEquals("", viewModel.uiState.value.draftMessage)
        assertTrue(viewModel.uiState.value.outputState is LogOutputState.ReviewReady)
    }

    @Test
    fun confirmSaveFromReviewReadyStateClearsReviewAndStoresSavedMealId() {
        val viewModel = createViewModel()

        viewModel.onEvent(LogUiEvent.DraftChanged("Chicken shawarma wrap"))
        viewModel.onEvent(LogUiEvent.SubmitClicked)
        viewModel.onEvent(LogUiEvent.ConfirmSaveClicked)

        assertEquals(LogOutputState.Empty, viewModel.uiState.value.outputState)
        assertTrue(viewModel.uiState.value.saveState is LogSaveState.Success)
    }

    private fun createViewModel(): LogViewModel =
        LogViewModel(
            dispatchers = dispatchers,
            prepareLogComposerSubmissionUseCase = PrepareLogComposerSubmissionUseCase(),
            interpretLogMealUseCase = InterpretLogMealUseCase(
                repository = FakeMealRepository(),
            ),
            confirmLogMealSaveUseCase = ConfirmLogMealSaveUseCase(
                mealRepository = FakeMealRepository(),
                dailyMetricsRepository = FakeDailyMetricsRepository(),
                nutritionGuidanceRepository = FakeNutritionGuidanceRepository(),
                userProfileRepository = FakeUserProfileRepository(),
                dailyMetricsCalculator = DailyMetricsCalculator(),
                timeProvider = object : TimeProvider {
                    override fun now(): Instant = Instant.parse("2026-04-21T12:00:00Z")
                },
            ),
        )

    private class FakeMealRepository : MealRepository {
        override fun observeMeals(): Flow<List<MealEntry>> = flowOf(emptyList())

        override suspend fun saveMealAndLoadMealsForDate(
            mealEntry: MealEntry,
            mealItems: List<MealItem>,
        ): AppResult<List<MealEntry>> = AppResult.Success(listOf(mealEntry))

        override suspend fun getMealsForDate(
            date: LocalDate,
        ): AppResult<List<MealEntry>> = AppResult.Success(emptyList())

        override suspend fun lookupLocalInterpretation(
            description: String,
        ): AppResult<LogMealReviewState?> = AppResult.Success(null)

        override suspend fun interpretWithGateway(
            description: String,
        ): AppResult<LogMealReviewState> = AppResult.Success(
            LogMealReviewState(
                submittedText = description,
                interpretationSource = LogMealInterpretationSource.AI_FALLBACK,
                items = listOf(
                    LogMealReviewItem(
                        displayName = "Chicken shawarma wrap",
                        portionDescription = "1 serving",
                        calories = 510,
                        protein = 24f,
                        carbs = 46f,
                        fat = 18f,
                        assumptions = "Used a simple serving estimate.",
                        confidence = 0.72f,
                    ),
                ),
                assumptions = listOf("Used a simple serving estimate."),
                requiresReview = true,
                confidence = 0.72f,
            ),
        )
    }

    private class FakeDailyMetricsRepository : DailyMetricsRepository {
        override suspend fun replaceDailyMetrics(metrics: DailyMetrics): AppResult<Unit> =
            AppResult.Success(Unit)
    }

    private class FakeNutritionGuidanceRepository : NutritionGuidanceRepository {
        override suspend fun getNutritionGuidance(userId: String): NutritionGuidance? =
            NutritionGuidance(
                userId = UserProfile.LOCAL_USER_ID,
                calorieTarget = 2200,
                suggestedProteinRange = "120-150g",
                suggestedCarbRange = "220-260g",
                suggestedFatRange = "60-75g",
                derivationExplanation = "Working target",
                notes = "",
                generatedAt = Instant.parse("2026-04-21T12:00:00Z"),
            )

        override fun observeNutritionGuidance(userId: String): Flow<NutritionGuidance?> =
            flowOf(null)

        override suspend fun refreshNutritionGuidance(profile: UserProfile): AppResult<NutritionGuidance> =
            AppResult.Failure(AppError.Unsupported("Not used in this test."))

        override suspend fun resetWorkingTargetToBaseline(profile: UserProfile): AppResult<NutritionGuidance> =
            AppResult.Failure(AppError.Unsupported("Not used in this test."))
    }

    private class FakeUserProfileRepository : UserProfileRepository {
        override suspend fun getUserProfile(userId: String): UserProfile =
            UserProfile(
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

        override fun observeUserProfile(userId: String): Flow<UserProfile?> = flowOf(null)

        override suspend fun upsertUserProfile(profile: UserProfile): AppResult<Unit> =
            AppResult.Failure(AppError.Unsupported("Not used in this test."))
    }
}
