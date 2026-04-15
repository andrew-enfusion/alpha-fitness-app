package com.andrewenfusion.alphafitness.data.gateway.onboarding.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingGuidanceAiContract(
    val contractVersion: Int,
    val actionType: String,
    val confidence: Float,
    val rationale: String,
    val requiresReview: Boolean,
    @SerialName("workingCalorieTarget") val workingCalorieTarget: Int,
    val calorieAdjustment: Int,
    val suggestedProteinRange: String,
    val suggestedCarbRange: String,
    val suggestedFatRange: String,
    val derivationExplanation: String,
    val notes: String? = null,
) {
    companion object {
        const val CONTRACT_VERSION: Int = 1
        const val ACTION_TYPE: String = "DERIVE_ONBOARDING_GUIDANCE"
    }
}
