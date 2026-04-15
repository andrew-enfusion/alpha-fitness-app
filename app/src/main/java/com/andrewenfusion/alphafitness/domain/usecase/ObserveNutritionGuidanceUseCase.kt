package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.domain.model.NutritionGuidance
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.domain.repository.NutritionGuidanceRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveNutritionGuidanceUseCase @Inject constructor(
    private val repository: NutritionGuidanceRepository,
) {
    operator fun invoke(userId: String = UserProfile.LOCAL_USER_ID): Flow<NutritionGuidance?> =
        repository.observeNutritionGuidance(userId)
}
