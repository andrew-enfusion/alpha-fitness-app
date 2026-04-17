package com.andrewenfusion.alphafitness.feature.onboarding

import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingUiStateTest {
    @Test
    fun exposesRetryOnlyWhenGuidanceRefreshFailedAndBaselineExists() {
        val state = validState().copy(
            calorieTarget = 2200,
            guidanceError = "Retry me",
        )

        assertTrue(state.canRetryGuidance)
    }

    @Test
    fun detectsWorkingTargetOverrideWhenTargetsDiffer() {
        val state = validState().copy(
            calorieTarget = 2200,
            guidanceCalorieTarget = 2350,
        )

        assertTrue(state.hasWorkingTargetOverride)
        assertTrue(state.canResetWorkingTarget)
    }

    @Test
    fun disablesRetryAndResetWhileGuidanceIsBusy() {
        val state = validState().copy(
            calorieTarget = 2200,
            guidanceCalorieTarget = 2350,
            guidanceError = "Retry me",
            isRefreshingGuidance = true,
        )

        assertFalse(state.canRetryGuidance)
        assertFalse(state.canResetWorkingTarget)
    }

    private fun validState(): OnboardingUiState =
        OnboardingUiState(
            age = "30",
            heightCm = "180",
            weightKg = "80",
            sex = Sex.MALE,
            exerciseLevel = ExerciseLevel.MODERATE,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
        )
}
