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

@Singleton
class DevelopmentOnboardingGuidanceGateway @Inject constructor(
    private val config: NutritionGuidanceConfig,
) : OnboardingGuidanceGateway {
    override suspend fun deriveGuidance(profile: UserProfile): AppResult<NutritionGuidanceDraft> {
        val split = config.macroSplit(profile.goalType)
        val adjustment = computeWorkingTargetAdjustment(profile)
        val workingTarget = profile.calorieTarget + adjustment.calorieDelta

        return AppResult.Success(
            NutritionGuidanceDraft(
                calorieTarget = workingTarget,
                suggestedProteinRange = config.formatMacroRange(
                    calorieTarget = workingTarget,
                    lowerRatio = split.proteinLower,
                    upperRatio = split.proteinUpper,
                    caloriesPerGram = 4f,
                ),
                suggestedCarbRange = config.formatMacroRange(
                    calorieTarget = workingTarget,
                    lowerRatio = split.carbLower,
                    upperRatio = split.carbUpper,
                    caloriesPerGram = 4f,
                ),
                suggestedFatRange = config.formatMacroRange(
                    calorieTarget = workingTarget,
                    lowerRatio = split.fatLower,
                    upperRatio = split.fatUpper,
                    caloriesPerGram = 9f,
                ),
                derivationExplanation = buildExplanation(
                    profile = profile,
                    workingTarget = workingTarget,
                    adjustment = adjustment,
                ),
                notes = buildNotes(adjustment),
            ),
        )
    }

    private fun buildExplanation(
        profile: UserProfile,
        workingTarget: Int,
        adjustment: WorkingTargetAdjustment,
    ): String {
        val goalText = when (profile.goalType) {
            GoalType.LOSE -> "a calorie deficit"
            GoalType.MAINTAIN -> "weight maintenance"
            GoalType.GAIN -> "a calorie surplus"
            GoalType.UNSPECIFIED -> "a stable starting point"
        }
        val activityText =
            "${profile.exerciseLevel.labelText()} exercise and ${profile.jobActivityLevel.labelText()} daily activity"

        return if (adjustment.calorieDelta == 0) {
            "Your working target currently matches the ${profile.calorieTarget} kcal/day deterministic baseline. This development guidance uses your saved profile, Mifflin-St Jeor baseline, and $activityText to frame macro guidance around $goalText without applying an additional adjustment yet."
        } else {
            "Your deterministic baseline remains ${profile.calorieTarget} kcal/day, while the current working target is $workingTarget kcal/day. This development guidance applies a ${signedDelta(adjustment.calorieDelta)} kcal/day adjustment to account for ${adjustment.reason} while framing macro guidance around $goalText."
        }
    }

    private fun buildNotes(adjustment: WorkingTargetAdjustment): String =
        if (adjustment.calorieDelta == 0) {
            "The current development guidance gateway did not apply a working-target adjustment, so the app uses the deterministic baseline as both the baseline and working target."
        } else {
            "This development guidance gateway is simulating a future provider-backed adjustment. Resetting guidance to baseline will copy the deterministic baseline target back into the working target."
        }

    private fun computeWorkingTargetAdjustment(profile: UserProfile): WorkingTargetAdjustment =
        when {
            profile.exerciseLevel == ExerciseLevel.ATHLETE &&
                profile.jobActivityLevel == JobActivityLevel.VERY_ACTIVE ->
                WorkingTargetAdjustment(
                    calorieDelta = config.athleteVeryActiveAdjustmentCalories(),
                    reason = "the combination of athlete-level training and a very active daily workload",
                )

            profile.exerciseLevel == ExerciseLevel.HIGH &&
                profile.jobActivityLevel == JobActivityLevel.VERY_ACTIVE ->
                WorkingTargetAdjustment(
                    calorieDelta = config.highVeryActiveAdjustmentCalories(),
                    reason = "the combination of high training volume and a very active daily workload",
                )

            profile.goalType == GoalType.LOSE &&
                profile.exerciseLevel == ExerciseLevel.LOW &&
                profile.jobActivityLevel == JobActivityLevel.SEDENTARY ->
                WorkingTargetAdjustment(
                    calorieDelta = config.lowSedentaryFatLossAdjustmentCalories(),
                    reason = "a conservative downward adjustment for a low-activity fat-loss profile",
                )

            else -> WorkingTargetAdjustment(
                calorieDelta = 0,
                reason = "no additional contextual adjustment",
            )
        }

    private fun signedDelta(value: Int): String =
        if (value >= 0) "+$value" else value.toString()
}

private data class WorkingTargetAdjustment(
    val calorieDelta: Int,
    val reason: String,
)

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
