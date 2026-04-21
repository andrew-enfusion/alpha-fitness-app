package com.andrewenfusion.alphafitness.data.mapper

import com.andrewenfusion.alphafitness.core.database.entity.DailyMetricsEntity
import com.andrewenfusion.alphafitness.domain.model.DailyMetrics
import java.time.Instant
import java.time.LocalDate

fun DailyMetrics.toEntity(): DailyMetricsEntity =
    DailyMetricsEntity(
        date = date.toString(),
        totalCalories = totalCalories,
        totalProtein = totalProtein,
        totalCarbs = totalCarbs,
        totalFat = totalFat,
        targetCalories = targetCalories,
        mealCount = mealCount,
        lastRecomputedAtEpochMillis = lastRecomputedAt.toEpochMilli(),
    )

fun DailyMetricsEntity.toDomain(): DailyMetrics =
    DailyMetrics(
        date = LocalDate.parse(date),
        totalCalories = totalCalories,
        totalProtein = totalProtein,
        totalCarbs = totalCarbs,
        totalFat = totalFat,
        targetCalories = targetCalories,
        mealCount = mealCount,
        lastRecomputedAt = Instant.ofEpochMilli(lastRecomputedAtEpochMillis),
    )
