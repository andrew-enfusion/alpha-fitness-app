package com.andrewenfusion.alphafitness.data.gateway.log

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationDraft
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewItem
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import javax.inject.Inject
import kotlin.math.roundToInt

class DevelopmentLogInterpretationGateway @Inject constructor() : LogInterpretationGateway {
    override suspend fun interpretMealDescription(
        description: String,
    ): AppResult<LogMealInterpretationDraft> {
        val isLowConfidenceInput = isLowConfidenceInput(description)
        val parts = description
            .split(",", " and ")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .ifEmpty { listOf(description.trim()) }

        val items = parts.map { part ->
            buildReviewItem(part)
        }

        return AppResult.Success(
            LogMealInterpretationDraft(
                reviewState = LogMealReviewState(
                    submittedText = description,
                    interpretationSource = LogMealInterpretationSource.AI_FALLBACK,
                    items = items,
                    assumptions = listOf(
                        if (isLowConfidenceInput) {
                            "Description was still broad, so this draft is a best-effort estimate."
                        } else {
                            "Used a simple single-serving estimate for each described food."
                        },
                        "Restaurant or homemade preparation details were not confirmed in this slice.",
                    ),
                    requiresReview = true,
                    confidence = if (isLowConfidenceInput) {
                        0.48f
                    } else {
                        0.72f
                    },
                ),
                clarificationNeeded = isLowConfidenceInput,
                clarificationQuestion = if (isLowConfidenceInput) {
                    "What was the main protein or style?"
                } else {
                    null
                },
            ),
        )
    }

    private fun buildReviewItem(
        rawPart: String,
    ): LogMealReviewItem {
        val normalizedPart = rawPart.lowercase()
        val calories = when {
            "fries" in normalizedPart -> 365
            "rice" in normalizedPart -> 240
            "burrito" in normalizedPart -> 520
            "wrap" in normalizedPart -> 460
            "shawarma" in normalizedPart -> 510
            "beef" in normalizedPart -> 330
            "vegetarian" in normalizedPart || "veggie" in normalizedPart -> 230
            "salad" in normalizedPart -> 220
            "yogurt" in normalizedPart -> 190
            "granola" in normalizedPart -> 210
            "chicken" in normalizedPart -> 280
            "coke" in normalizedPart || "soda" in normalizedPart -> 140
            "diet coke" in normalizedPart || "diet soda" in normalizedPart -> 5
            else -> 260
        }
        val protein = when {
            "chicken" in normalizedPart || "shawarma" in normalizedPart -> 24f
            "beef" in normalizedPart -> 22f
            "vegetarian" in normalizedPart || "veggie" in normalizedPart -> 10f
            "yogurt" in normalizedPart -> 14f
            else -> (calories * 0.08f / 4f).roundToInt().toFloat()
        }
        val carbs = when {
            "rice" in normalizedPart -> 42f
            "fries" in normalizedPart -> 48f
            "granola" in normalizedPart -> 30f
            "coke" in normalizedPart || "soda" in normalizedPart -> 35f
            else -> (calories * 0.45f / 4f).roundToInt().toFloat()
        }
        val fat = when {
            "fries" in normalizedPart -> 17f
            "shawarma" in normalizedPart || "wrap" in normalizedPart || "burrito" in normalizedPart -> 18f
            else -> (calories * 0.27f / 9f).roundToInt().toFloat()
        }

        return LogMealReviewItem(
            displayName = rawPart.replaceFirstChar { it.uppercase() },
            portionDescription = "1 serving",
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            assumptions = "Used a simple single-serving estimate for this item.",
            confidence = 0.72f,
        )
    }

    private fun isLowConfidenceInput(
        description: String,
    ): Boolean {
        val normalizedDescription = description.lowercase()
        val tokenCount = normalizedDescription
            .split(" ", ",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .size

        return tokenCount <= 2 && lowConfidenceTerms.any { it in normalizedDescription }
    }

    private companion object {
        val lowConfidenceTerms = setOf(
            "meal",
            "food",
            "stuff",
            "something",
            "snack",
            "plate",
            "bowl",
            "sandwich",
            "wrap",
            "burger",
            "burrito",
            "pasta",
            "salad",
            "drink",
        )
    }
}
