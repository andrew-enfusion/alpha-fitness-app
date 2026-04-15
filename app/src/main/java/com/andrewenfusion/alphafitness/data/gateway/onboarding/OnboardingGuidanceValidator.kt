package com.andrewenfusion.alphafitness.data.gateway.onboarding

import com.andrewenfusion.alphafitness.data.gateway.onboarding.model.OnboardingGuidanceAiContract
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidanceDraft
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.core.config.NutritionGuidanceConfig
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class OnboardingGuidanceValidator @Inject constructor(
    private val config: NutritionGuidanceConfig,
) {
    fun toDraftOrNull(
        contract: OnboardingGuidanceAiContract,
        profile: UserProfile,
    ): NutritionGuidanceDraft? {
        if (contract.contractVersion != OnboardingGuidanceAiContract.CONTRACT_VERSION) return null
        if (contract.actionType != OnboardingGuidanceAiContract.ACTION_TYPE) return null

        val clampedConfidence = contract.confidence.coerceIn(0f, 1f)
        val correctedWorkingTarget = contract.workingCalorieTarget
            .coerceIn(
                config.minimumWorkingTargetCalories(),
                config.maximumWorkingTargetCalories(),
            )
        val correctedAdjustment = (correctedWorkingTarget - profile.calorieTarget)
            .coerceIn(
                -config.maximumAiAdjustmentMagnitudeCalories(),
                config.maximumAiAdjustmentMagnitudeCalories(),
            )
        val finalWorkingTarget =
            (profile.calorieTarget + correctedAdjustment).coerceIn(
                config.minimumWorkingTargetCalories(),
                config.maximumWorkingTargetCalories(),
            )

        if (contract.derivationExplanation.isBlank()) return null
        if (contract.suggestedProteinRange.isBlank()) return null
        if (contract.suggestedCarbRange.isBlank()) return null
        if (contract.suggestedFatRange.isBlank()) return null

        val alignedTarget =
            if (abs(finalWorkingTarget - correctedWorkingTarget) > 0) finalWorkingTarget else correctedWorkingTarget

        return NutritionGuidanceDraft(
            calorieTarget = alignedTarget,
            suggestedProteinRange = contract.suggestedProteinRange,
            suggestedCarbRange = contract.suggestedCarbRange,
            suggestedFatRange = contract.suggestedFatRange,
            derivationExplanation = contract.derivationExplanation,
            notes = buildString {
                append(contract.notes.orEmpty())
                if (isNotEmpty()) {
                    append(" ")
                }
                append("AI confidence: ${String.format(Locale.US, "%.2f", clampedConfidence)}.")
            }.trim(),
        )
    }
}
