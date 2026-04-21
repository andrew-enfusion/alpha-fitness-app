package com.andrewenfusion.alphafitness.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_item",
    foreignKeys = [
        ForeignKey(
            entity = MealEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealEntryId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["mealEntryId"]),
    ],
)
data class MealItemEntity(
    @PrimaryKey val id: String,
    val mealEntryId: String,
    val foodReferenceId: String?,
    val displayName: String,
    val quantity: Float?,
    val unit: String?,
    val portionDescription: String?,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val assumptions: String,
    val confidence: Float,
)
