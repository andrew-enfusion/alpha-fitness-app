package com.andrewenfusion.alphafitness.core.config

import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex

object CalorieTargetConfig {
    const val BASE_ACTIVITY_MULTIPLIER: Double = 1.2
    const val ROUNDING_INCREMENT: Int = 25

    const val MALE_BMR_OFFSET: Double = 5.0
    const val FEMALE_BMR_OFFSET: Double = -161.0

    val exerciseLevelAdjustments: Map<ExerciseLevel, Double> = mapOf(
        ExerciseLevel.LOW to 0.0,
        ExerciseLevel.MODERATE to 0.175,
        ExerciseLevel.HIGH to 0.325,
        ExerciseLevel.ATHLETE to 0.475,
    )

    val jobActivityAdjustments: Map<JobActivityLevel, Double> = mapOf(
        JobActivityLevel.SEDENTARY to 0.0,
        JobActivityLevel.LIGHT to 0.1,
        JobActivityLevel.ACTIVE to 0.2,
        JobActivityLevel.VERY_ACTIVE to 0.3,
    )

    val goalAdjustments: Map<GoalType, Int> = mapOf(
        GoalType.LOSE to -300,
        GoalType.MAINTAIN to 0,
        GoalType.GAIN to 300,
    )

    val supportedSexes: Set<Sex> = setOf(
        Sex.FEMALE,
        Sex.MALE,
    )
}
