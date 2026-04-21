package com.andrewenfusion.alphafitness.domain.model

data class LogMealReviewState(
    val submittedText: String,
    val interpretationSource: LogMealInterpretationSource,
    val items: List<LogMealReviewItem>,
    val assumptions: List<String>,
    val requiresReview: Boolean,
    val confidence: Float,
) {
    val totalCalories: Int
        get() = items.sumOf { it.calories }

    val totalProtein: Float
        get() = items.sumOf { it.protein.toDouble() }.toFloat()

    val totalCarbs: Float
        get() = items.sumOf { it.carbs.toDouble() }.toFloat()

    val totalFat: Float
        get() = items.sumOf { it.fat.toDouble() }.toFloat()
}
