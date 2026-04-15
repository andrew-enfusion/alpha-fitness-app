package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidance
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.domain.repository.NutritionGuidanceRepository
import javax.inject.Inject

class ResetNutritionGuidanceToBaselineUseCase @Inject constructor(
    private val repository: NutritionGuidanceRepository,
) {
    suspend operator fun invoke(profile: UserProfile): AppResult<NutritionGuidance> =
        repository.resetWorkingTargetToBaseline(profile)
}
