package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.config.LogClarificationConfig
import com.andrewenfusion.alphafitness.core.common.time.TimeProvider
import com.andrewenfusion.alphafitness.core.config.LocalMealMemoryConfig
import com.andrewenfusion.alphafitness.domain.engine.LocalMealMemoryMatcher
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewItem
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import com.andrewenfusion.alphafitness.domain.model.MealItem
import com.andrewenfusion.alphafitness.domain.model.SavedMealMemory
import com.andrewenfusion.alphafitness.domain.model.SavedMealMemoryItem
import com.andrewenfusion.alphafitness.domain.repository.MealRepository
import com.andrewenfusion.alphafitness.feature.log.LogOutputState
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InterpretLogMealUseCaseTest {
    @Test
    fun returnsLocalMatchBeforeGatewayFallback() = runBlocking {
        val repository = FakeMealRepository(
            savedMeals = listOf(
                SavedMealMemory(
                    mealEntryId = "meal-1",
                    rawInput = "Greek yogurt with granola",
                    timestamp = Instant.parse("2026-04-20T08:00:00Z"),
                    items = listOf(
                        SavedMealMemoryItem(
                            displayName = "Greek yogurt",
                            portionDescription = "1 serving",
                            calories = 190,
                            protein = 14f,
                            carbs = 12f,
                            fat = 5f,
                            assumptions = "Used the last confirmed serving.",
                            confidence = 0.9f,
                        ),
                        SavedMealMemoryItem(
                            displayName = "Granola",
                            portionDescription = "1 handful",
                            calories = 210,
                            protein = 5f,
                            carbs = 30f,
                            fat = 8f,
                            assumptions = "Used the last confirmed serving.",
                            confidence = 0.88f,
                        ),
                    ),
                ),
            ),
        )
        val useCase = InterpretLogMealUseCase(
            repository = repository,
            localMealMemoryMatcher = LocalMealMemoryMatcher(LocalMealMemoryConfig()),
            localMealMemoryConfig = LocalMealMemoryConfig(),
            logClarificationConfig = LogClarificationConfig(),
            timeProvider = fixedTimeProvider(),
        )

        val result = useCase("Greek yogurt with granola")

        assertTrue(result is AppResult.Success)
        val outputState = (result as AppResult.Success).value
        assertTrue(outputState is LogOutputState.ReviewReady)
        val review = (outputState as LogOutputState.ReviewReady).reviewState
        assertEquals(LogMealInterpretationSource.LOCAL_MATCH, review.interpretationSource)
        assertEquals("Greek yogurt with granola", review.submittedText)
        assertEquals(2, review.items.size)
        assertEquals(30, repository.requestedLimit)
    }

    @Test
    fun fallsBackToGatewayWhenLocalMatchIsUnavailable() = runBlocking {
        val gatewayReview = sampleReview(
            submittedText = "Chicken burrito bowl",
            source = LogMealInterpretationSource.AI_FALLBACK,
        )
        val useCase = InterpretLogMealUseCase(
            repository = FakeMealRepository(
                gatewayReview = gatewayReview,
            ),
            localMealMemoryMatcher = LocalMealMemoryMatcher(LocalMealMemoryConfig()),
            localMealMemoryConfig = LocalMealMemoryConfig(),
            logClarificationConfig = LogClarificationConfig(),
            timeProvider = fixedTimeProvider(),
        )

        val result = useCase("Chicken burrito bowl")

        assertTrue(result is AppResult.Success)
        val outputState = (result as AppResult.Success).value
        assertTrue(outputState is LogOutputState.ReviewReady)
        val review = (outputState as LogOutputState.ReviewReady).reviewState
        assertEquals(LogMealInterpretationSource.AI_FALLBACK, review.interpretationSource)
        assertEquals("Chicken burrito bowl", review.submittedText)
    }

    @Test
    fun returnsLowConfidenceBeforeReviewReadyWhenGatewayDraftIsTooBroad() = runBlocking {
        val useCase = InterpretLogMealUseCase(
            repository = FakeMealRepository(
                gatewayReview = sampleReview(
                    submittedText = "sandwich",
                    source = LogMealInterpretationSource.AI_FALLBACK,
                    confidence = 0.48f,
                ),
            ),
            localMealMemoryMatcher = LocalMealMemoryMatcher(LocalMealMemoryConfig()),
            localMealMemoryConfig = LocalMealMemoryConfig(),
            logClarificationConfig = LogClarificationConfig(),
            timeProvider = fixedTimeProvider(),
        )

        val result = useCase("sandwich")

        assertTrue(result is AppResult.Success)
        val outputState = (result as AppResult.Success).value
        assertTrue(outputState is LogOutputState.LowConfidence)
        val clarificationState = (outputState as LogOutputState.LowConfidence).clarificationState
        assertEquals("sandwich", clarificationState.originalSubmittedDraft)
        assertEquals(3, clarificationState.quickOptions.size)
    }

    @Test
    fun clarificationAnswerProducesReviewReadyWithoutSecondClarificationCycle() = runBlocking {
        val useCase = InterpretLogMealUseCase(
            repository = FakeMealRepository(
                gatewayReview = sampleReview(
                    submittedText = "Chicken sandwich",
                    source = LogMealInterpretationSource.AI_FALLBACK,
                    confidence = 0.55f,
                ),
            ),
            localMealMemoryMatcher = LocalMealMemoryMatcher(LocalMealMemoryConfig()),
            localMealMemoryConfig = LocalMealMemoryConfig(),
            logClarificationConfig = LogClarificationConfig(),
            timeProvider = fixedTimeProvider(),
        )

        val result = useCase(
            originalSubmittedDraft = "sandwich",
            clarificationAnswer = "Chicken",
        )

        assertTrue(result is AppResult.Success)
        val outputState = (result as AppResult.Success).value
        assertTrue(outputState is LogOutputState.ReviewReady)
        val reviewState = (outputState as LogOutputState.ReviewReady).reviewState
        assertEquals("sandwich", reviewState.submittedText)
        assertTrue(reviewState.assumptions.any { "Clarification used: Chicken." in it })
        assertTrue(reviewState.assumptions.any { "best-effort estimate" in it })
    }

    private fun sampleReview(
        submittedText: String,
        source: LogMealInterpretationSource,
        confidence: Float = 0.8f,
    ): LogMealReviewState =
        LogMealReviewState(
            submittedText = submittedText,
            interpretationSource = source,
            items = listOf(
                LogMealReviewItem(
                    displayName = "Sample Item",
                    portionDescription = "1 serving",
                    calories = 320,
                    protein = 18f,
                        carbs = 24f,
                        fat = 12f,
                        confidence = confidence,
                    ),
            ),
            assumptions = listOf("Sample assumption"),
            requiresReview = true,
            confidence = confidence,
        )

    private fun fixedTimeProvider(): TimeProvider =
        object : TimeProvider {
            override fun now(): Instant = Instant.parse("2026-04-21T12:00:00Z")
        }

    private class FakeMealRepository(
        private val savedMeals: List<SavedMealMemory> = emptyList(),
        private val gatewayReview: LogMealReviewState = LogMealReviewState(
            submittedText = "fallback",
            interpretationSource = LogMealInterpretationSource.AI_FALLBACK,
            items = emptyList(),
            assumptions = emptyList(),
            requiresReview = true,
            confidence = 0.5f,
        ),
    ) : MealRepository {
        var requestedLimit: Int? = null

        override fun observeMeals(): Flow<List<MealEntry>> = flowOf(emptyList())

        override suspend fun saveMealAndLoadMealsForDate(
            mealEntry: MealEntry,
            mealItems: List<MealItem>,
        ): AppResult<List<MealEntry>> = AppResult.Success(emptyList())

        override suspend fun getMealsForDate(
            date: LocalDate,
        ): AppResult<List<MealEntry>> = AppResult.Success(emptyList())

        override suspend fun getRecentSavedMeals(
            limit: Int,
        ): AppResult<List<SavedMealMemory>> {
            requestedLimit = limit
            return AppResult.Success(savedMeals.take(limit))
        }

        override suspend fun interpretWithGateway(
            description: String,
        ): AppResult<LogMealReviewState> = AppResult.Success(
            gatewayReview.copy(submittedText = description),
        )
    }
}
