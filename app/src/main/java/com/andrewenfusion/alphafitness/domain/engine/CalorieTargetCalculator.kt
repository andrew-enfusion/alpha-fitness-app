package com.andrewenfusion.alphafitness.domain.engine

import com.andrewenfusion.alphafitness.core.config.CalorieTargetConfig
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex
import javax.inject.Inject
import kotlin.math.roundToInt

class CalorieTargetCalculator @Inject constructor() {
    fun calculate(
        age: Int,
        sex: Sex,
        heightCm: Float,
        weightKg: Float,
        exerciseLevel: ExerciseLevel,
        jobActivityLevel: JobActivityLevel,
        goalType: GoalType,
    ): Int {
        val bmr = (10.0 * weightKg) +
            (6.25 * heightCm) -
            (5.0 * age) +
            sex.bmrOffset()
        val activityMultiplier = CalorieTargetConfig.BASE_ACTIVITY_MULTIPLIER +
            exerciseLevel.activityAdjustment() +
            jobActivityLevel.activityAdjustment()
        val baselineTarget = bmr * activityMultiplier
        val adjustedTarget = baselineTarget + goalType.goalAdjustment()

        return adjustedTarget.roundToNearest(
            increment = CalorieTargetConfig.ROUNDING_INCREMENT,
        )
    }

    private fun Sex.bmrOffset(): Double =
        when (this) {
            Sex.FEMALE -> CalorieTargetConfig.FEMALE_BMR_OFFSET
            Sex.MALE -> CalorieTargetConfig.MALE_BMR_OFFSET
            Sex.UNSPECIFIED -> error("Unsupported sex for deterministic calorie derivation")
        }

    private fun ExerciseLevel.activityAdjustment(): Double =
        CalorieTargetConfig.exerciseLevelAdjustments.getValue(this)

    private fun JobActivityLevel.activityAdjustment(): Double =
        CalorieTargetConfig.jobActivityAdjustments.getValue(this)

    private fun GoalType.goalAdjustment(): Int =
        CalorieTargetConfig.goalAdjustments.getValue(this)

    private fun Double.roundToNearest(increment: Int): Int =
        ((this / increment).roundToInt() * increment)
}
