package com.andrewenfusion.alphafitness.data.mapper

import com.andrewenfusion.alphafitness.core.database.entity.NutritionGuidanceEntity
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidance
import java.time.Instant

fun NutritionGuidanceEntity.toDomain(): NutritionGuidance =
    NutritionGuidance(
        userId = userId,
        calorieTarget = calorieTarget,
        suggestedProteinRange = suggestedProteinRange,
        suggestedCarbRange = suggestedCarbRange,
        suggestedFatRange = suggestedFatRange,
        derivationExplanation = derivationExplanation,
        notes = notes,
        generatedAt = Instant.ofEpochMilli(generatedAtEpochMillis),
    )

fun NutritionGuidance.toEntity(): NutritionGuidanceEntity =
    NutritionGuidanceEntity(
        userId = userId,
        calorieTarget = calorieTarget,
        suggestedProteinRange = suggestedProteinRange,
        suggestedCarbRange = suggestedCarbRange,
        suggestedFatRange = suggestedFatRange,
        derivationExplanation = derivationExplanation,
        notes = notes,
        generatedAtEpochMillis = generatedAt.toEpochMilli(),
    )
