package com.andrewenfusion.alphafitness.domain.engine

import com.andrewenfusion.alphafitness.domain.model.DailyMetrics
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class DailyMetricsCalculator @Inject constructor() {
    fun calculate(
        date: LocalDate,
        meals: List<MealEntry>,
        targetCalories: Int,
        recomputedAt: Instant,
    ): DailyMetrics =
        DailyMetrics(
            date = date,
            totalCalories = meals.sumOf { it.totalCalories },
            totalProtein = meals.sumOf { it.totalProtein.toDouble() }.toFloat(),
            totalCarbs = meals.sumOf { it.totalCarbs.toDouble() }.toFloat(),
            totalFat = meals.sumOf { it.totalFat.toDouble() }.toFloat(),
            targetCalories = targetCalories,
            mealCount = meals.size,
            lastRecomputedAt = recomputedAt,
        )
}
