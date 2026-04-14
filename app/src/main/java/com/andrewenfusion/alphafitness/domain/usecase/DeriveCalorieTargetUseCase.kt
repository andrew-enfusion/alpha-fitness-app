package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.config.CalorieTargetConfig
import com.andrewenfusion.alphafitness.domain.engine.CalorieTargetCalculator
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex
import javax.inject.Inject

class DeriveCalorieTargetUseCase @Inject constructor(
    private val calculator: CalorieTargetCalculator,
) {
    operator fun invoke(
        age: Int,
        sex: Sex,
        heightCm: Float,
        weightKg: Float,
        exerciseLevel: ExerciseLevel,
        jobActivityLevel: JobActivityLevel,
        goalType: GoalType,
    ): AppResult<Int> {
        if (age <= 0) {
            return AppResult.Failure(AppError.Validation(message = "Age must be greater than zero."))
        }
        if (heightCm <= 0f) {
            return AppResult.Failure(AppError.Validation(message = "Height must be greater than zero."))
        }
        if (weightKg <= 0f) {
            return AppResult.Failure(AppError.Validation(message = "Weight must be greater than zero."))
        }
        if (sex !in CalorieTargetConfig.supportedSexes) {
            return AppResult.Failure(AppError.Validation(message = "Select male or female to derive your baseline calorie target."))
        }
        if (exerciseLevel == ExerciseLevel.UNSPECIFIED) {
            return AppResult.Failure(AppError.Validation(message = "Choose an exercise level before deriving your calorie target."))
        }
        if (jobActivityLevel == JobActivityLevel.UNSPECIFIED) {
            return AppResult.Failure(AppError.Validation(message = "Choose a job activity level before deriving your calorie target."))
        }
        if (goalType == GoalType.UNSPECIFIED) {
            return AppResult.Failure(AppError.Validation(message = "Choose a goal before deriving your calorie target."))
        }

        return AppResult.Success(
            calculator.calculate(
                age = age,
                sex = sex,
                heightCm = heightCm,
                weightKg = weightKg,
                exerciseLevel = exerciseLevel,
                jobActivityLevel = jobActivityLevel,
                goalType = goalType,
            ),
        )
    }
}
