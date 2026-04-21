package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewItem
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import com.andrewenfusion.alphafitness.domain.model.MealItem
import com.andrewenfusion.alphafitness.domain.repository.MealRepository
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
        val localReview = sampleReview(
            submittedText = "Greek yogurt with granola",
            source = LogMealInterpretationSource.LOCAL_MATCH,
        )
        val useCase = InterpretLogMealUseCase(
            repository = FakeMealRepository(
                localReview = localReview,
            ),
        )

        val result = useCase("Greek yogurt with granola")

        assertTrue(result is AppResult.Success)
        val review = (result as AppResult.Success).value
        assertEquals(LogMealInterpretationSource.LOCAL_MATCH, review.interpretationSource)
        assertEquals("Greek yogurt with granola", review.submittedText)
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
        )

        val result = useCase("Chicken burrito bowl")

        assertTrue(result is AppResult.Success)
        val review = (result as AppResult.Success).value
        assertEquals(LogMealInterpretationSource.AI_FALLBACK, review.interpretationSource)
        assertEquals("Chicken burrito bowl", review.submittedText)
    }

    private fun sampleReview(
        submittedText: String,
        source: LogMealInterpretationSource,
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
                    confidence = 0.8f,
                ),
            ),
            assumptions = listOf("Sample assumption"),
            requiresReview = true,
            confidence = 0.8f,
        )

    private class FakeMealRepository(
        private val localReview: LogMealReviewState? = null,
        private val gatewayReview: LogMealReviewState = LogMealReviewState(
            submittedText = "fallback",
            interpretationSource = LogMealInterpretationSource.AI_FALLBACK,
            items = emptyList(),
            assumptions = emptyList(),
            requiresReview = true,
            confidence = 0.5f,
        ),
    ) : MealRepository {
        override fun observeMeals(): Flow<List<MealEntry>> = flowOf(emptyList())

        override suspend fun saveMealAndLoadMealsForDate(
            mealEntry: MealEntry,
            mealItems: List<MealItem>,
        ): AppResult<List<MealEntry>> = AppResult.Success(emptyList())

        override suspend fun getMealsForDate(
            date: LocalDate,
        ): AppResult<List<MealEntry>> = AppResult.Success(emptyList())

        override suspend fun lookupLocalInterpretation(
            description: String,
        ): AppResult<LogMealReviewState?> = AppResult.Success(localReview)

        override suspend fun interpretWithGateway(
            description: String,
        ): AppResult<LogMealReviewState> = AppResult.Success(
            gatewayReview.copy(submittedText = description),
        )
    }
}
