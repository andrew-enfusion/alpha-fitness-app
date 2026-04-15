package com.andrewenfusion.alphafitness.domain.model

data class NutritionGuidanceDraft(
    val calorieTarget: Int,
    val suggestedProteinRange: String,
    val suggestedCarbRange: String,
    val suggestedFatRange: String,
    val derivationExplanation: String,
    val notes: String,
)
