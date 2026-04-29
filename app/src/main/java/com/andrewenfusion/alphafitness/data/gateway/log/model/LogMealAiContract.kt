package com.andrewenfusion.alphafitness.data.gateway.log.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogMealAiContract(
    val contractVersion: Int,
    val actionType: String,
    val confidence: Float,
    val rationale: String,
    val requiresReview: Boolean,
    val mealItems: List<LogMealAiItem>,
    val clarificationNeeded: Boolean,
    val clarificationQuestion: String? = null,
    val mealTimeHint: String? = null,
    val sourceAssumptions: List<String> = emptyList(),
) {
    companion object {
        const val CONTRACT_VERSION: Int = 1
        const val ACTION_TYPE: String = "LOG_MEAL"
    }
}

@Serializable
data class LogMealAiItem(
    val name: String,
    val quantity: Float? = null,
    val unit: String? = null,
    val portionDescription: String? = null,
    val preparationType: String? = null,
    val contextHints: List<String> = emptyList(),
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val confidence: Float,
)
