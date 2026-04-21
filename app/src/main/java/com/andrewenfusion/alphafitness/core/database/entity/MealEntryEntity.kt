package com.andrewenfusion.alphafitness.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_entry")
data class MealEntryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val date: String,
    val timestampEpochMillis: Long,
    val rawInput: String?,
    val sourceType: String,
    val photoUri: String?,
    val totalCalories: Int,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val confidence: Float,
    val isUserEdited: Boolean,
    val reviewRequired: Boolean,
)
