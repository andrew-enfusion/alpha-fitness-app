package com.andrewenfusion.alphafitness.data.repository.model

import androidx.room.Embedded
import androidx.room.Relation
import com.andrewenfusion.alphafitness.core.database.entity.MealEntryEntity
import com.andrewenfusion.alphafitness.core.database.entity.MealItemEntity

data class MealEntryWithItems(
    @Embedded val mealEntry: MealEntryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "mealEntryId",
    )
    val mealItems: List<MealItemEntity>,
)
