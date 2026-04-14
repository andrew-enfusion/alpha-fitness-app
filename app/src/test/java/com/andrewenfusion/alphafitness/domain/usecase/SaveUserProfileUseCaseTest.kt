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
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SaveUserProfileUseCaseTest {
    @Test
    fun usesPendingCalorieTargetForFirstSave() = runBlocking {
        val repository = FakeUserProfileRepository()
        val useCase = SaveUserProfileUseCase(
            repository = repository,
            timeProvider = FakeTimeProvider(Instant.parse("2026-04-14T12:00:00Z")),
        )

        useCase(
            age = 30,
            sex = Sex.MALE,
            heightCm = 180f,
            weightKg = 80f,
            exerciseLevel = ExerciseLevel.MODERATE,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
        )

        val saved = repository.savedProfile
        assertNotNull(saved)
        assertEquals(OnboardingConfig.PENDING_CALORIE_TARGET, saved?.calorieTarget)
    }

    @Test
    fun preservesCreatedAtAndExistingCalorieTargetWhenUpdating() = runBlocking {
        val existing = UserProfile(
            age = 25,
            sex = Sex.FEMALE,
            heightCm = 165f,
            weightKg = 60f,
            exerciseLevel = ExerciseLevel.LOW,
            jobActivityLevel = JobActivityLevel.LIGHT,
            goalType = GoalType.LOSE,
            calorieTarget = 2100,
            createdAt = Instant.parse("2026-04-13T10:00:00Z"),
            updatedAt = Instant.parse("2026-04-13T10:00:00Z"),
        )
        val repository = FakeUserProfileRepository(existing)
        val now = Instant.parse("2026-04-14T12:00:00Z")
        val useCase = SaveUserProfileUseCase(
            repository = repository,
            timeProvider = FakeTimeProvider(now),
        )

        useCase(
            age = 26,
            sex = Sex.FEMALE,
            heightCm = 166f,
            weightKg = 61f,
            exerciseLevel = ExerciseLevel.MODERATE,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
        )

        val saved = repository.savedProfile
        assertEquals(existing.createdAt, saved?.createdAt)
        assertEquals(existing.calorieTarget, saved?.calorieTarget)
        assertEquals(now, saved?.updatedAt)
    }
}

private class FakeUserProfileRepository(
    private var existingProfile: UserProfile? = null,
) : UserProfileRepository {
    var savedProfile: UserProfile? = null
        private set

    override suspend fun getUserProfile(userId: String): UserProfile? = existingProfile

    override fun observeUserProfile(userId: String): Flow<UserProfile?> = flowOf(existingProfile)

    override suspend fun upsertUserProfile(profile: UserProfile): AppResult<Unit> {
        savedProfile = profile
        existingProfile = profile
        return AppResult.Success(Unit)
    }
}

private class FakeTimeProvider(
    private val instant: Instant,
) : TimeProvider {
    override fun now(): Instant = instant
}
