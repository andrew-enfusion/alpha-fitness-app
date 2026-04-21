package com.andrewenfusion.alphafitness.domain.model

data class SavedMealMemoryItem(
    val displayName: String,
    val portionDescription: String?,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val assumptions: String,
    val confidence: Float,
)
