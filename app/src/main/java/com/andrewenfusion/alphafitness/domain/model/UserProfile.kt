package com.andrewenfusion.alphafitness.domain.model

import java.time.Instant

data class UserProfile(
    val id: String = LOCAL_USER_ID,
    val age: Int,
    val sex: Sex,
    val heightCm: Float,
    val weightKg: Float,
    val exerciseLevel: ExerciseLevel,
    val jobActivityLevel: JobActivityLevel,
    val goalType: GoalType,
    val calorieTarget: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    val isSetupComplete: Boolean
        get() = age > 0 &&
            heightCm > 0f &&
            weightKg > 0f &&
            sex != Sex.UNSPECIFIED &&
            exerciseLevel != ExerciseLevel.UNSPECIFIED &&
            jobActivityLevel != JobActivityLevel.UNSPECIFIED &&
            goalType != GoalType.UNSPECIFIED &&
            calorieTarget > 0

    companion object {
        const val LOCAL_USER_ID: String = "local_user"
    }
}
