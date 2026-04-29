package com.andrewenfusion.alphafitness.data.gateway.log

import com.andrewenfusion.alphafitness.data.gateway.log.model.LogMealAiContract
import com.andrewenfusion.alphafitness.data.gateway.log.model.LogMealAiItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class LogInterpretationValidatorTest {
    private val validator = LogInterpretationValidator()

    @Test
    fun returnsDraftForValidLogMealContract() {
        val draft = validator.toDraftOrNull(validContract())

        assertNotNull(draft)
        assertEquals(1, draft?.reviewState?.items?.size)
        assertEquals(false, draft?.clarificationNeeded)
        assertEquals(510, draft?.reviewState?.totalCalories)
    }

    @Test
    fun returnsNullWhenClarificationQuestionMissingForClarificationRequest() {
        val contract = validContract().copy(
            clarificationNeeded = true,
            clarificationQuestion = null,
        )

        val draft = validator.toDraftOrNull(contract)

        assertNull(draft)
    }

    @Test
    fun normalizesNegativeNutritionValuesAndClampsConfidence() {
        val contract = validContract().copy(
            confidence = 1.4f,
            mealItems = listOf(
                LogMealAiItem(
                    name = "Chicken wrap",
                    portionDescription = "",
                    preparationType = "grilled",
                    contextHints = listOf("restaurant"),
                    calories = -10,
                    protein = -5f,
                    carbs = 46f,
                    fat = -2f,
                    confidence = 1.3f,
                ),
            ),
        )

        val draft = validator.toDraftOrNull(contract)

        assertNotNull(draft)
        assertEquals(0, draft?.reviewState?.items?.first()?.calories)
        assertEquals(0f, draft?.reviewState?.items?.first()?.protein)
        assertEquals(0f, draft?.reviewState?.items?.first()?.fat)
        assertEquals(1f, draft?.reviewState?.confidence)
        assertEquals(1f, draft?.reviewState?.items?.first()?.confidence)
    }

    private fun validContract(): LogMealAiContract =
        LogMealAiContract(
            contractVersion = LogMealAiContract.CONTRACT_VERSION,
            actionType = LogMealAiContract.ACTION_TYPE,
            confidence = 0.82f,
            rationale = "Estimated a common restaurant portion.",
            requiresReview = true,
            mealItems = listOf(
                LogMealAiItem(
                    name = "Chicken wrap",
                    quantity = 1f,
                    unit = "serving",
                    portionDescription = "1 wrap",
                    preparationType = "grilled",
                    contextHints = listOf("restaurant"),
                    calories = 510,
                    protein = 24f,
                    carbs = 46f,
                    fat = 18f,
                    confidence = 0.78f,
                ),
            ),
            clarificationNeeded = false,
            clarificationQuestion = null,
            mealTimeHint = "lunch",
            sourceAssumptions = listOf("Used a standard restaurant serving estimate."),
        )
}
