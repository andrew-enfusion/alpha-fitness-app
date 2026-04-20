package com.andrewenfusion.alphafitness.feature.log

import com.andrewenfusion.alphafitness.core.common.dispatcher.AppDispatchers
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewItem
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.repository.MealRepository
import com.andrewenfusion.alphafitness.domain.usecase.InterpretLogMealUseCase
import com.andrewenfusion.alphafitness.domain.usecase.PrepareLogComposerSubmissionUseCase
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import com.andrewenfusion.alphafitness.core.common.result.AppResult
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

    private fun createViewModel(): LogViewModel =
        LogViewModel(
            dispatchers = dispatchers,
            prepareLogComposerSubmissionUseCase = PrepareLogComposerSubmissionUseCase(),
            interpretLogMealUseCase = InterpretLogMealUseCase(
                repository = FakeMealRepository(),
            ),
        )

    private class FakeMealRepository : MealRepository {
        override fun observeMeals(): Flow<List<MealEntry>> = flowOf(emptyList())

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
                    ),
                ),
                assumptions = listOf("Used a simple serving estimate."),
                requiresReview = true,
            ),
        )
    }
}
