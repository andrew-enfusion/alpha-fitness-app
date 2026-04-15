package com.andrewenfusion.alphafitness.data.gateway.onboarding

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.config.OpenAiOnboardingConfig
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidanceDraft
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfiguredOnboardingGuidanceGateway @Inject constructor(
    private val config: OpenAiOnboardingConfig,
    private val openAiGateway: OpenAiOnboardingGuidanceGateway,
    private val developmentGateway: DevelopmentOnboardingGuidanceGateway,
) : OnboardingGuidanceGateway {
    override suspend fun deriveGuidance(profile: UserProfile): AppResult<NutritionGuidanceDraft> =
        if (config.isConfigured) {
            openAiGateway.deriveGuidance(profile)
        } else {
            developmentGateway.deriveGuidance(profile)
        }
}
