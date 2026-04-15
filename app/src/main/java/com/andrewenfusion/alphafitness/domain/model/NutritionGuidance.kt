package com.andrewenfusion.alphafitness.domain.model

import java.time.Instant

data class NutritionGuidance(
    val userId: String,
    val calorieTarget: Int,
    val suggestedProteinRange: String,
    val suggestedCarbRange: String,
    val suggestedFatRange: String,
    val derivationExplanation: String,
    val notes: String,
    val generatedAt: Instant,
)
