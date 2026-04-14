package com.andrewenfusion.alphafitness.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val age: Int,
    val sex: String,
    val heightCm: Float,
    val weightKg: Float,
    val exerciseLevel: String,
    val jobActivityLevel: String,
    val goalType: String,
    val calorieTarget: Int,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
