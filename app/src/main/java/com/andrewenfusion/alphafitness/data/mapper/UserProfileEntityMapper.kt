package com.andrewenfusion.alphafitness.data.mapper

import com.andrewenfusion.alphafitness.core.database.entity.UserProfileEntity
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import java.time.Instant

fun UserProfileEntity.toDomain(): UserProfile =
    UserProfile(
        id = id,
        age = age,
        sex = sex.toEnum(Sex.UNSPECIFIED),
        heightCm = heightCm,
        weightKg = weightKg,
        exerciseLevel = exerciseLevel.toEnum(ExerciseLevel.UNSPECIFIED),
        jobActivityLevel = jobActivityLevel.toEnum(JobActivityLevel.UNSPECIFIED),
        goalType = goalType.toEnum(GoalType.UNSPECIFIED),
        calorieTarget = calorieTarget,
        createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
        updatedAt = Instant.ofEpochMilli(updatedAtEpochMillis),
    )

fun UserProfile.toEntity(): UserProfileEntity =
    UserProfileEntity(
        id = id,
        age = age,
        sex = sex.name,
        heightCm = heightCm,
        weightKg = weightKg,
        exerciseLevel = exerciseLevel.name,
        jobActivityLevel = jobActivityLevel.name,
        goalType = goalType.name,
        calorieTarget = calorieTarget,
        createdAtEpochMillis = createdAt.toEpochMilli(),
        updatedAtEpochMillis = updatedAt.toEpochMilli(),
    )

private inline fun <reified T : Enum<T>> String.toEnum(defaultValue: T): T =
    runCatching { enumValueOf<T>(this) }.getOrElse { defaultValue }
