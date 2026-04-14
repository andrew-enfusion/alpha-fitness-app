package com.andrewenfusion.alphafitness.domain.model

import java.time.Instant
import java.time.LocalDate

data class DailyMetrics(
    val date: LocalDate,
    val totalCalories: Int,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val targetCalories: Int,
    val mealCount: Int,
    val lastRecomputedAt: Instant,
)
