package com.andrewenfusion.alphafitness.data.gateway.log

import com.andrewenfusion.alphafitness.data.gateway.log.model.LogMealAiContract
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationDraft
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewItem
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogInterpretationValidator @Inject constructor() {
    fun toDraftOrNull(
        contract: LogMealAiContract,
    ): LogMealInterpretationDraft? {
        if (contract.contractVersion != LogMealAiContract.CONTRACT_VERSION) return null
        if (contract.actionType != LogMealAiContract.ACTION_TYPE) return null
        if (contract.rationale.isBlank()) return null
        if (contract.mealItems.isEmpty()) return null
        if (contract.clarificationNeeded && contract.clarificationQuestion.isNullOrBlank()) return null

        val items = contract.mealItems.map { item ->
            val displayName = item.name.trim()
            if (displayName.isBlank()) return null

            val portionDescription = item.portionDescription
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: buildPortionDescription(
                    quantity = item.quantity,
                    unit = item.unit,
                )

            LogMealReviewItem(
                displayName = displayName,
                portionDescription = portionDescription,
                calories = item.calories.coerceAtLeast(0),
                protein = item.protein.coerceAtLeast(0f),
                carbs = item.carbs.coerceAtLeast(0f),
                fat = item.fat.coerceAtLeast(0f),
                assumptions = buildItemAssumptions(
                    preparationType = item.preparationType,
                    contextHints = item.contextHints,
                ),
                confidence = item.confidence.coerceIn(0f, 1f),
            )
        }

        val assumptions = contract.sourceAssumptions
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .ifEmpty { listOf("Used provider estimates for this draft.") }

        val reviewState = LogMealReviewState(
            submittedText = "",
            interpretationSource = LogMealInterpretationSource.AI_FALLBACK,
            items = items,
            assumptions = assumptions,
            requiresReview = true,
            confidence = contract.confidence.coerceIn(0f, 1f),
        )

        return LogMealInterpretationDraft(
            reviewState = reviewState,
            clarificationNeeded = contract.clarificationNeeded,
            clarificationQuestion = contract.clarificationQuestion?.trim()?.takeIf { it.isNotBlank() },
        )
    }

    private fun buildPortionDescription(
        quantity: Float?,
        unit: String?,
    ): String {
        val normalizedUnit = unit?.trim()?.takeIf { it.isNotBlank() }
        return if (quantity != null && normalizedUnit != null) {
            "$quantity $normalizedUnit"
        } else {
            "1 serving"
        }
    }

    private fun buildItemAssumptions(
        preparationType: String?,
        contextHints: List<String>,
    ): String {
        val parts = buildList {
            preparationType?.trim()?.takeIf { it.isNotBlank() }?.let { add("Preparation: $it.") }
            val hints = contextHints.map { it.trim() }.filter { it.isNotBlank() }
            if (hints.isNotEmpty()) {
                add("Context: ${hints.joinToString()}.")
            }
        }
        return parts.joinToString(" ").ifBlank {
            "Used provider estimate for this item."
        }
    }
}
