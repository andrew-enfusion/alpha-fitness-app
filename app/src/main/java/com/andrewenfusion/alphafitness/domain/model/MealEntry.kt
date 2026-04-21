package com.andrewenfusion.alphafitness.domain.model

import java.time.Instant
import java.time.LocalDate

data class MealEntry(
    val id: String,
    val userId: String,
    val date: LocalDate,
    val timestamp: Instant,
    val rawInput: String?,
    val sourceType: MealSourceType,
    val photoUri: String?,
    val totalCalories: Int,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val confidence: Float,
    val isUserEdited: Boolean,
    val reviewRequired: Boolean,
)
