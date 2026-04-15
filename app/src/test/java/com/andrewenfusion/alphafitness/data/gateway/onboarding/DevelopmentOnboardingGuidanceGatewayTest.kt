package com.andrewenfusion.alphafitness.data.gateway.onboarding

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.config.NutritionGuidanceConfig
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import java.time.Instant
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DevelopmentOnboardingGuidanceGatewayTest {
    private val gateway = DevelopmentOnboardingGuidanceGateway(
        config = NutritionGuidanceConfig(),
    )

    @Test
    fun keepsDeterministicBaselineTargetAndBuildsMacroRanges() = runBlocking {
        val profile = UserProfile(
            age = 30,
            sex = Sex.MALE,
            heightCm = 180f,
            weightKg = 80f,
            exerciseLevel = ExerciseLevel.MODERATE,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
            calorieTarget = 2800,
            createdAt = Instant.parse("2026-04-14T12:00:00Z"),
            updatedAt = Instant.parse("2026-04-14T12:00:00Z"),
        )

        val result = gateway.deriveGuidance(profile)

        assertTrue(result is AppResult.Success)
        val guidance = (result as AppResult.Success).value
        assertEquals(2800, guidance.calorieTarget)
        assertEquals("175-210 g/day", guidance.suggestedProteinRange)
        assertEquals("280-315 g/day", guidance.suggestedCarbRange)
        assertEquals("80-95 g/day", guidance.suggestedFatRange)
        assertTrue(guidance.derivationExplanation.contains("2800 kcal/day"))
    }
}
