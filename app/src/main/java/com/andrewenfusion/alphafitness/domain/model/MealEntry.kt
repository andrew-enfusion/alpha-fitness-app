package com.andrewenfusion.alphafitness.domain.model

import java.time.Instant

data class MealEntry(
    val id: String,
    val timestamp: Instant,
    val sourceType: MealSourceType,
    val totalCalories: Int,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val confidence: Float,
    val reviewRequired: Boolean,
)
