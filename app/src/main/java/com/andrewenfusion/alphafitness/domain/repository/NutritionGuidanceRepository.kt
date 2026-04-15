package com.andrewenfusion.alphafitness.domain.repository

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidance
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface NutritionGuidanceRepository {
    fun observeNutritionGuidance(userId: String = UserProfile.LOCAL_USER_ID): Flow<NutritionGuidance?>

    suspend fun refreshNutritionGuidance(profile: UserProfile): AppResult<NutritionGuidance>
}
