package com.andrewenfusion.alphafitness.domain.model

data class LogMealReviewItem(
    val displayName: String,
    val portionDescription: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
)
