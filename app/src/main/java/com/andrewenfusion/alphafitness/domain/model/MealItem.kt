package com.andrewenfusion.alphafitness.domain.model

data class MealItem(
    val id: String,
    val mealEntryId: String,
    val foodReferenceId: String?,
    val displayName: String,
    val quantity: Float?,
    val unit: String?,
    val portionDescription: String?,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val assumptions: String,
    val confidence: Float,
)
