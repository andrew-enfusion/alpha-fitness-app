package com.andrewenfusion.alphafitness.data.mapper

import com.andrewenfusion.alphafitness.core.database.entity.MealEntryEntity
import com.andrewenfusion.alphafitness.core.database.entity.MealItemEntity
import com.andrewenfusion.alphafitness.domain.model.MealEntry
import com.andrewenfusion.alphafitness.domain.model.MealItem
import com.andrewenfusion.alphafitness.domain.model.MealSourceType
import java.time.Instant
import java.time.LocalDate

fun MealEntryEntity.toDomain(): MealEntry =
    MealEntry(
        id = id,
        userId = userId,
        date = LocalDate.parse(date),
        timestamp = Instant.ofEpochMilli(timestampEpochMillis),
        rawInput = rawInput,
        sourceType = sourceType.toEnum(MealSourceType.TEXT),
        photoUri = photoUri,
        totalCalories = totalCalories,
        totalProtein = totalProtein,
        totalCarbs = totalCarbs,
        totalFat = totalFat,
        confidence = confidence,
        isUserEdited = isUserEdited,
        reviewRequired = reviewRequired,
    )

fun MealEntry.toEntity(): MealEntryEntity =
    MealEntryEntity(
        id = id,
        userId = userId,
        date = date.toString(),
        timestampEpochMillis = timestamp.toEpochMilli(),
        rawInput = rawInput,
        sourceType = sourceType.name,
        photoUri = photoUri,
        totalCalories = totalCalories,
        totalProtein = totalProtein,
        totalCarbs = totalCarbs,
        totalFat = totalFat,
        confidence = confidence,
        isUserEdited = isUserEdited,
        reviewRequired = reviewRequired,
    )

fun MealItem.toEntity(): MealItemEntity =
    MealItemEntity(
        id = id,
        mealEntryId = mealEntryId,
        foodReferenceId = foodReferenceId,
        displayName = displayName,
        quantity = quantity,
        unit = unit,
        portionDescription = portionDescription,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fat = fat,
        assumptions = assumptions,
        confidence = confidence,
    )

private inline fun <reified T : Enum<T>> String.toEnum(defaultValue: T): T =
    runCatching { enumValueOf<T>(this) }.getOrElse { defaultValue }
