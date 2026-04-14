package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.common.time.TimeProvider
import com.andrewenfusion.alphafitness.core.config.OnboardingConfig
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.domain.repository.UserProfileRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(
        age: Int,
        sex: Sex,
        heightCm: Float,
        weightKg: Float,
        exerciseLevel: ExerciseLevel,
        jobActivityLevel: JobActivityLevel,
        goalType: GoalType,
    ): AppResult<Unit> {
        val existing = repository.getUserProfile()
        val now = timeProvider.now()

        val profile = UserProfile(
            id = existing?.id ?: UserProfile.LOCAL_USER_ID,
            age = age,
            sex = sex,
            heightCm = heightCm,
            weightKg = weightKg,
            exerciseLevel = exerciseLevel,
            jobActivityLevel = jobActivityLevel,
            goalType = goalType,
            calorieTarget = existing?.calorieTarget ?: OnboardingConfig.PENDING_CALORIE_TARGET,
            createdAt = existing?.createdAt ?: now,
            updatedAt = now,
        )

        return repository.upsertUserProfile(profile)
    }
}
