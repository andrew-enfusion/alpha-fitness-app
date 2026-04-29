package com.andrewenfusion.alphafitness.feature.log

import com.andrewenfusion.alphafitness.core.common.dispatcher.AppDispatchers
import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.time.TimeProvider
import com.andrewenfusion.alphafitness.core.config.LogClarificationConfig
import com.andrewenfusion.alphafitness.core.config.LocalMealMemoryConfig
import com.andrewenfusion.alphafitness.data.gateway.log.LogInterpretationGateway
import com.andrewenfusion.alphafitness.domain.engine.LocalMealMemoryMatcher
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationDraft
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewItem
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.model.MealItem
import com.andrewenfusion.alphafitness.domain.model.SavedMealMemory
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
        assertEquals(null, viewModel.uiState.value.submittedDraft)
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
    fun submitWithBroadDraftShowsLowConfidenceClarificationState() {
        val viewModel = createViewModel(
            mealRepository = FakeMealRepository(
                gatewayResults = mutableListOf(
                    AppResult.Success(
                        sampleDraft(
                            submittedText = "sandwich",
                            confidence = 0.48f,
                            clarificationNeeded = true,
                        ),
                    ),
                ),
            ),
        )

        viewModel.onEvent(LogUiEvent.DraftChanged("sandwich"))
        viewModel.onEvent(LogUiEvent.SubmitClicked)

        assertTrue(viewModel.uiState.value.outputState is LogOutputState.LowConfidence)
        val clarificationState =
            (viewModel.uiState.value.outputState as LogOutputState.LowConfidence).clarificationState
        assertEquals("sandwich", clarificationState.originalSubmittedDraft)
        assertEquals("", viewModel.uiState.value.draftMessage)
        assertEquals("sandwich", viewModel.uiState.value.submittedDraft)
    }

    @Test
    fun retryUsesOriginalSubmittedDraftAfterTimeoutFailure() {
        val mealRepository = FakeMealRepository(
            gatewayResults = mutableListOf(
                AppResult.Failure(AppError.AiTimeout()),
                AppResult.Success(sampleDraft("Chicken shawarma wrap")),
            ),
        )
        val viewModel = createViewModel(
            mealRepository = mealRepository,
        )

        viewModel.onEvent(LogUiEvent.DraftChanged("Chicken shawarma wrap"))
        viewModel.onEvent(LogUiEvent.SubmitClicked)
        viewModel.onEvent(LogUiEvent.DraftChanged("Edited meal that should not be retried"))
        viewModel.onEvent(LogUiEvent.RetryInterpretationClicked)

        assertEquals(
            listOf("Chicken shawarma wrap", "Chicken shawarma wrap"),
            mealRepository.interpretRequests,
        )
        assertEquals(
            "Edited meal that should not be retried",
            viewModel.uiState.value.draftMessage,
        )
        assertTrue(viewModel.uiState.value.outputState is LogOutputState.ReviewReady)
    }

    @Test
    fun submitWithMalformedResultMapsToMalformedFailureState() {
        val viewModel = createViewModel(
            mealRepository = FakeMealRepository(
                gatewayResults = mutableListOf(
                    AppResult.Failure(AppError.AiMalformedResponse()),
                ),
            ),
        )

        viewModel.onEvent(LogUiEvent.DraftChanged("Chicken shawarma wrap"))
        viewModel.onEvent(LogUiEvent.SubmitClicked)

        assertTrue(viewModel.uiState.value.outputState is LogOutputState.InterpretationMalformed)
    }

    @Test
    fun quickOptionClarificationUsesOriginalSubmittedDraftAndProducesReviewReadyState() {
        val mealRepository = FakeMealRepository(
            gatewayResults = mutableListOf(
                AppResult.Success(
                    sampleDraft(
                        submittedText = "sandwich",
                        confidence = 0.48f,
                        clarificationNeeded = true,
                    ),
                ),
                AppResult.Success(
                    sampleDraft(
                        submittedText = "Chicken sandwich",
                        confidence = 0.55f,
                    ),
                ),
            ),
        )
        val viewModel = createViewModel(mealRepository = mealRepository)

        viewModel.onEvent(LogUiEvent.DraftChanged("sandwich"))
        viewModel.onEvent(LogUiEvent.SubmitClicked)
        viewModel.onEvent(LogUiEvent.ClarificationOptionSelected("Chicken"))

        assertEquals(listOf("sandwich", "Chicken sandwich"), mealRepository.interpretRequests)
        assertTrue(viewModel.uiState.value.outputState is LogOutputState.ReviewReady)
        val reviewState =
            (viewModel.uiState.value.outputState as LogOutputState.ReviewReady).reviewState
        assertEquals("sandwich", reviewState.submittedText)
        assertTrue(reviewState.assumptions.any { "Clarification used: Chicken." in it })
    }

    @Test
    fun textClarificationSubmitsWithoutOverwritingOriginalDraft() {
        val mealRepository = FakeMealRepository(
            gatewayResults = mutableListOf(
                AppResult.Success(
                    sampleDraft(
                        submittedText = "sandwich",
                        confidence = 0.48f,
                        clarificationNeeded = true,
                    ),
                ),
                AppResult.Success(
                    sampleDraft(
                        submittedText = "Turkey sandwich",
                        confidence = 0.55f,
                    ),
                ),
            ),
        )
        val viewModel = createViewModel(mealRepository = mealRepository)

        viewModel.onEvent(LogUiEvent.DraftChanged("sandwich"))
        viewModel.onEvent(LogUiEvent.SubmitClicked)
        viewModel.onEvent(LogUiEvent.ClarificationDraftChanged("Turkey"))
        viewModel.onEvent(LogUiEvent.SubmitClarificationClicked)

        assertEquals("sandwich", viewModel.uiState.value.submittedDraft)
        assertEquals("", viewModel.uiState.value.clarificationDraft)
        assertEquals(listOf("sandwich", "Turkey sandwich"), mealRepository.interpretRequests)
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

    @Test
    fun saveFailureKeepsReviewReadyStateSeparateFromInterpretationFailure() {
        val mealRepository = FakeMealRepository(
            saveFailure = AppError.Storage("Failed to store the reviewed meal."),
        )
        val viewModel = createViewModel(
            mealRepository = mealRepository,
        )

        viewModel.onEvent(LogUiEvent.DraftChanged("Chicken shawarma wrap"))
        viewModel.onEvent(LogUiEvent.SubmitClicked)
        viewModel.onEvent(LogUiEvent.ConfirmSaveClicked)

        assertTrue(viewModel.uiState.value.outputState is LogOutputState.ReviewReady)
        assertEquals(
            LogSaveState.Failure("Failed to store the reviewed meal."),
            viewModel.uiState.value.saveState,
        )
    }

    private fun createViewModel(
        mealRepository: FakeMealRepository = FakeMealRepository(),
    ): LogViewModel =
        LogViewModel(
            dispatchers = dispatchers,
            prepareLogComposerSubmissionUseCase = PrepareLogComposerSubmissionUseCase(),
            interpretLogMealUseCase = InterpretLogMealUseCase(
                repository = mealRepository,
                logInterpretationGateway = mealRepository,
                localMealMemoryMatcher = LocalMealMemoryMatcher(LocalMealMemoryConfig()),
                localMealMemoryConfig = LocalMealMemoryConfig(),
                logClarificationConfig = LogClarificationConfig(),
                timeProvider = object : TimeProvider {
                    override fun now(): Instant = Instant.parse("2026-04-21T12:00:00Z")
                },
            ),
            confirmLogMealSaveUseCase = ConfirmLogMealSaveUseCase(
                mealRepository = mealRepository,
                dailyMetricsRepository = FakeDailyMetricsRepository(),
                nutritionGuidanceRepository = FakeNutritionGuidanceRepository(),
                userProfileRepository = FakeUserProfileRepository(),
                dailyMetricsCalculator = DailyMetricsCalculator(),
                timeProvider = object : TimeProvider {
                    override fun now(): Instant = Instant.parse("2026-04-21T12:00:00Z")
                },
            ),
        )

    private class FakeMealRepository(
        private val gatewayResults: MutableList<AppResult<LogMealInterpretationDraft>> = mutableListOf(
            AppResult.Success(sampleDraft("Chicken shawarma wrap")),
        ),
        private val saveFailure: AppError? = null,
    ) : MealRepository, LogInterpretationGateway {
        val interpretRequests: MutableList<String> = mutableListOf()

        override fun observeMeals(): Flow<List<MealEntry>> = flowOf(emptyList())

        override suspend fun saveMealAndLoadMealsForDate(
            mealEntry: MealEntry,
            mealItems: List<MealItem>,
        ): AppResult<List<MealEntry>> =
            saveFailure?.let { AppResult.Failure(it) } ?: AppResult.Success(listOf(mealEntry))

        override suspend fun getMealsForDate(
            date: LocalDate,
        ): AppResult<List<MealEntry>> = AppResult.Success(emptyList())

        override suspend fun getRecentSavedMeals(
            limit: Int,
        ): AppResult<List<SavedMealMemory>> = AppResult.Success(emptyList())

        override suspend fun interpretMealDescription(
            description: String,
        ): AppResult<LogMealInterpretationDraft> {
            interpretRequests += description
            val nextResult = gatewayResults.removeFirstOrNull()
                ?: AppResult.Success(sampleDraft(description))
            return when (nextResult) {
                is AppResult.Success -> AppResult.Success(
                    nextResult.value.copy(
                        reviewState = nextResult.value.reviewState.copy(
                            submittedText = description,
                        ),
                    ),
                )
                is AppResult.Failure -> nextResult
            }
        }
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

    private companion object {
        fun sampleReviewState(
            submittedText: String,
            confidence: Float = 0.72f,
        ): LogMealReviewState =
            LogMealReviewState(
                submittedText = submittedText,
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
                        confidence = confidence,
                    ),
                ),
                assumptions = listOf("Used a simple serving estimate."),
                requiresReview = true,
                confidence = confidence,
            )

        fun sampleDraft(
            submittedText: String,
            confidence: Float = 0.72f,
            clarificationNeeded: Boolean = false,
        ): LogMealInterpretationDraft =
            LogMealInterpretationDraft(
                reviewState = sampleReviewState(
                    submittedText = submittedText,
                    confidence = confidence,
                ),
                clarificationNeeded = clarificationNeeded,
                clarificationQuestion = if (clarificationNeeded) {
                    "What was the main protein or style?"
                } else {
                    null
                },
            )
    }
}
