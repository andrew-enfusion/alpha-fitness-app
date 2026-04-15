package com.andrewenfusion.alphafitness.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_guidance")
data class NutritionGuidanceEntity(
    @PrimaryKey val userId: String,
    val calorieTarget: Int,
    val suggestedProteinRange: String,
    val suggestedCarbRange: String,
    val suggestedFatRange: String,
    val derivationExplanation: String,
    val notes: String,
    val generatedAtEpochMillis: Long,
)
