package com.andrewenfusion.alphafitness.data.gateway.onboarding

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidanceDraft
import com.andrewenfusion.alphafitness.domain.model.UserProfile

interface OnboardingGuidanceGateway {
    suspend fun deriveGuidance(profile: UserProfile): AppResult<NutritionGuidanceDraft>
}
