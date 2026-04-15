package com.andrewenfusion.alphafitness.data.gateway.onboarding

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.config.NutritionGuidanceConfig
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidanceDraft
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class DevelopmentOnboardingGuidanceGateway @Inject constructor(
    private val config: NutritionGuidanceConfig,
) : OnboardingGuidanceGateway {
    override suspend fun deriveGuidance(profile: UserProfile): AppResult<NutritionGuidanceDraft> {
        val split = config.macroSplit(profile.goalType)

        return AppResult.Success(
            NutritionGuidanceDraft(
                calorieTarget = profile.calorieTarget,
                suggestedProteinRange = formatMacroRange(
                    calorieTarget = profile.calorieTarget,
                    lowerRatio = split.proteinLower,
                    upperRatio = split.proteinUpper,
                    caloriesPerGram = 4f,
                ),
                suggestedCarbRange = formatMacroRange(
                    calorieTarget = profile.calorieTarget,
                    lowerRatio = split.carbLower,
                    upperRatio = split.carbUpper,
                    caloriesPerGram = 4f,
                ),
                suggestedFatRange = formatMacroRange(
                    calorieTarget = profile.calorieTarget,
                    lowerRatio = split.fatLower,
                    upperRatio = split.fatUpper,
                    caloriesPerGram = 9f,
                ),
                derivationExplanation = buildExplanation(profile),
                notes = "This development guidance keeps your deterministic baseline target unchanged while the onboarding guidance gateway is still running through a local implementation.",
            ),
        )
    }

    private fun buildExplanation(profile: UserProfile): String {
        val goalText = when (profile.goalType) {
            GoalType.LOSE -> "a calorie deficit"
            GoalType.MAINTAIN -> "weight maintenance"
            GoalType.GAIN -> "a calorie surplus"
            GoalType.UNSPECIFIED -> "a stable starting point"
        }
        val activityText =
            "${profile.exerciseLevel.labelText()} exercise and ${profile.jobActivityLevel.labelText()} daily activity"

        return "Your current baseline stays at ${profile.calorieTarget} kcal/day. This onboarding guidance uses your saved profile, Mifflin-St Jeor baseline, and $activityText to frame macro guidance around $goalText without overriding the app-derived target yet."
    }

    private fun formatMacroRange(
        calorieTarget: Int,
        lowerRatio: Float,
        upperRatio: Float,
        caloriesPerGram: Float,
    ): String {
        val lower = roundToNearestFive((calorieTarget * lowerRatio) / caloriesPerGram)
        val upper = roundToNearestFive((calorieTarget * upperRatio) / caloriesPerGram)
        return "$lower-$upper g/day"
    }

    private fun roundToNearestFive(value: Float): Int =
        (value / 5f).roundToInt() * 5
}

private fun ExerciseLevel.labelText(): String =
    when (this) {
        ExerciseLevel.LOW -> "low"
        ExerciseLevel.MODERATE -> "moderate"
        ExerciseLevel.HIGH -> "high"
        ExerciseLevel.ATHLETE -> "athlete-level"
        ExerciseLevel.UNSPECIFIED -> "unspecified"
    }

private fun JobActivityLevel.labelText(): String =
    when (this) {
        JobActivityLevel.SEDENTARY -> "sedentary"
        JobActivityLevel.LIGHT -> "light"
        JobActivityLevel.ACTIVE -> "active"
        JobActivityLevel.VERY_ACTIVE -> "very active"
        JobActivityLevel.UNSPECIFIED -> "unspecified"
    }
