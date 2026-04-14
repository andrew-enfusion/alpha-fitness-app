package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.engine.CalorieTargetCalculator
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeriveCalorieTargetUseCaseTest {
    private val useCase = DeriveCalorieTargetUseCase(
        calculator = CalorieTargetCalculator(),
    )

    @Test
    fun derivesRoundedMaintainTargetForMaleProfile() {
        val result = useCase(
            age = 30,
            sex = Sex.MALE,
            heightCm = 180f,
            weightKg = 80f,
            exerciseLevel = ExerciseLevel.MODERATE,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
        )

        assertEquals(AppResult.Success(2800), result)
    }

    @Test
    fun derivesRoundedMaintainTargetForFemaleProfile() {
        val result = useCase(
            age = 26,
            sex = Sex.FEMALE,
            heightCm = 166f,
            weightKg = 61f,
            exerciseLevel = ExerciseLevel.MODERATE,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
        )

        assertEquals(AppResult.Success(2125), result)
    }

    @Test
    fun rejectsUnsupportedSexForDeterministicBaseline() {
        val result = useCase(
            age = 30,
            sex = Sex.UNSPECIFIED,
            heightCm = 180f,
            weightKg = 80f,
            exerciseLevel = ExerciseLevel.MODERATE,
            jobActivityLevel = JobActivityLevel.ACTIVE,
            goalType = GoalType.MAINTAIN,
        )

        assertTrue(result is AppResult.Failure)
    }
}
