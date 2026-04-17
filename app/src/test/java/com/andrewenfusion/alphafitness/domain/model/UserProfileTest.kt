package com.andrewenfusion.alphafitness.domain.model

import java.time.Instant
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserProfileTest {
    @Test
    fun reportsSetupCompleteWhenRequiredFieldsArePresent() {
        val profile = validProfile()

        assertTrue(profile.isSetupComplete)
    }

    @Test
    fun reportsSetupIncompleteWhenGoalTypeIsMissing() {
        val profile = validProfile().copy(goalType = GoalType.UNSPECIFIED)

        assertFalse(profile.isSetupComplete)
    }

    private fun validProfile(): UserProfile =
        UserProfile(
            age = 30,
            sex = Sex.MALE,
            heightCm = 180f,
            weightKg = 82f,
            exerciseLevel = ExerciseLevel.MODERATE,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
            calorieTarget = 2400,
            createdAt = Instant.parse("2026-04-17T10:00:23Z"),
            updatedAt = Instant.parse("2026-04-17T10:00:23Z"),
        )
}
