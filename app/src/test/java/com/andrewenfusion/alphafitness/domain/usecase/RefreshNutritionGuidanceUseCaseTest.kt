package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidance
import com.andrewenfusion.alphafitness.domain.model.Sex
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.domain.repository.NutritionGuidanceRepository
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RefreshNutritionGuidanceUseCaseTest {
    @Test
    fun returnsPersistedGuidanceFromRepository() = runBlocking {
        val repository = FakeNutritionGuidanceRepository()
        val useCase = RefreshNutritionGuidanceUseCase(repository)
        val profile = UserProfile(
            age = 26,
            sex = Sex.FEMALE,
            heightCm = 166f,
            weightKg = 61f,
            exerciseLevel = ExerciseLevel.MODERATE,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
            calorieTarget = 2125,
            createdAt = Instant.parse("2026-04-14T12:00:00Z"),
            updatedAt = Instant.parse("2026-04-14T12:00:00Z"),
        )

        val result = useCase(profile)

        assertEquals(profile, repository.refreshedProfile)
        assertEquals(AppResult.Success(repository.guidance), result)
    }

    @Test
    fun resetUseCaseCopiesBaselineTargetIntoGuidance() = runBlocking {
        val repository = FakeNutritionGuidanceRepository()
        val useCase = ResetNutritionGuidanceToBaselineUseCase(repository)
        val profile = UserProfile(
            age = 26,
            sex = Sex.FEMALE,
            heightCm = 166f,
            weightKg = 61f,
            exerciseLevel = ExerciseLevel.MODERATE,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
            calorieTarget = 2125,
            createdAt = Instant.parse("2026-04-14T12:00:00Z"),
            updatedAt = Instant.parse("2026-04-14T12:00:00Z"),
        )

        val result = useCase(profile)

        assertEquals(profile, repository.resetProfile)
        assertEquals(2125, repository.resetGuidance?.calorieTarget)
        assertEquals(AppResult.Success(repository.resetGuidance!!), result)
    }
}

private class FakeNutritionGuidanceRepository : NutritionGuidanceRepository {
    val guidance = NutritionGuidance(
        userId = UserProfile.LOCAL_USER_ID,
        calorieTarget = 2125,
        suggestedProteinRange = "135-160 g/day",
        suggestedCarbRange = "210-240 g/day",
        suggestedFatRange = "60-70 g/day",
        derivationExplanation = "Saved profile guidance.",
        notes = "Local fake guidance.",
        generatedAt = Instant.parse("2026-04-14T12:05:00Z"),
    )

    var refreshedProfile: UserProfile? = null
        private set
    var resetProfile: UserProfile? = null
        private set
    var resetGuidance: NutritionGuidance? = null
        private set

    override suspend fun getNutritionGuidance(userId: String): NutritionGuidance? = guidance

    override fun observeNutritionGuidance(userId: String): Flow<NutritionGuidance?> =
        flowOf(guidance)

    override suspend fun refreshNutritionGuidance(profile: UserProfile): AppResult<NutritionGuidance> {
        refreshedProfile = profile
        return AppResult.Success(guidance)
    }

    override suspend fun resetWorkingTargetToBaseline(profile: UserProfile): AppResult<NutritionGuidance> {
        resetProfile = profile
        resetGuidance = guidance.copy(
            calorieTarget = profile.calorieTarget,
            derivationExplanation = "Reset to baseline.",
        )
        return AppResult.Success(resetGuidance!!)
    }
}
