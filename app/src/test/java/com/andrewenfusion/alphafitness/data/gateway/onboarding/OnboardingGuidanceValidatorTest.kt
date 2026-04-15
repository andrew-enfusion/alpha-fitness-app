package com.andrewenfusion.alphafitness.data.gateway.onboarding

import com.andrewenfusion.alphafitness.core.config.NutritionGuidanceConfig
import com.andrewenfusion.alphafitness.data.gateway.onboarding.model.OnboardingGuidanceAiContract
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class OnboardingGuidanceValidatorTest {
    private val validator = OnboardingGuidanceValidator(
        config = NutritionGuidanceConfig(),
    )

    @Test
    fun returnsDraftForValidStructuredOutput() {
        val profile = sampleProfile()
        val contract = OnboardingGuidanceAiContract(
            contractVersion = OnboardingGuidanceAiContract.CONTRACT_VERSION,
            actionType = OnboardingGuidanceAiContract.ACTION_TYPE,
            confidence = 0.82f,
            rationale = "High workload adjustment.",
            requiresReview = false,
            workingCalorieTarget = 2950,
            calorieAdjustment = 150,
            suggestedProteinRange = "185-220 g/day",
            suggestedCarbRange = "295-330 g/day",
            suggestedFatRange = "80-100 g/day",
            derivationExplanation = "Working target raised to support a very active training load.",
            notes = "Provider guidance.",
        )

        val draft = validator.toDraftOrNull(contract, profile)

        assertNotNull(draft)
        assertEquals(2950, draft?.calorieTarget)
        assertEquals("Provider guidance. AI confidence: 0.82.", draft?.notes)
    }

    @Test
    fun clampsExcessiveAdjustmentBackIntoConfiguredRange() {
        val profile = sampleProfile()
        val contract = OnboardingGuidanceAiContract(
            contractVersion = OnboardingGuidanceAiContract.CONTRACT_VERSION,
            actionType = OnboardingGuidanceAiContract.ACTION_TYPE,
            confidence = 1.4f,
            rationale = "Extreme adjustment.",
            requiresReview = false,
            workingCalorieTarget = 4200,
            calorieAdjustment = 1400,
            suggestedProteinRange = "200-240 g/day",
            suggestedCarbRange = "400-450 g/day",
            suggestedFatRange = "90-110 g/day",
            derivationExplanation = "Target increased aggressively.",
            notes = null,
        )

        val draft = validator.toDraftOrNull(contract, profile)

        assertNotNull(draft)
        assertEquals(3300, draft?.calorieTarget)
        assertEquals("AI confidence: 1.00.", draft?.notes)
    }

    private fun sampleProfile(): UserProfile =
        UserProfile(
            age = 30,
            sex = Sex.MALE,
            heightCm = 180f,
            weightKg = 80f,
            exerciseLevel = ExerciseLevel.HIGH,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
            calorieTarget = 2800,
            createdAt = Instant.parse("2026-04-15T00:00:00Z"),
            updatedAt = Instant.parse("2026-04-15T00:00:00Z"),
        )
}
