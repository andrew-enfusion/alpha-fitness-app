package com.andrewenfusion.alphafitness.data.repository

import com.andrewenfusion.alphafitness.core.common.dispatcher.AppDispatchers
import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.common.time.TimeProvider
import com.andrewenfusion.alphafitness.core.database.dao.NutritionGuidanceDao
import com.andrewenfusion.alphafitness.data.gateway.onboarding.OnboardingGuidanceGateway
import com.andrewenfusion.alphafitness.data.mapper.toDomain
import com.andrewenfusion.alphafitness.data.mapper.toEntity
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidance
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.domain.repository.NutritionGuidanceRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomNutritionGuidanceRepository @Inject constructor(
    private val guidanceDao: NutritionGuidanceDao,
    private val guidanceGateway: OnboardingGuidanceGateway,
    private val timeProvider: TimeProvider,
    private val dispatchers: AppDispatchers,
) : NutritionGuidanceRepository {
    override fun observeNutritionGuidance(userId: String): Flow<NutritionGuidance?> =
        guidanceDao
            .observeByUserId(userId)
            .map { entity -> entity?.toDomain() }
            .flowOn(dispatchers.io)

    override suspend fun refreshNutritionGuidance(profile: UserProfile): AppResult<NutritionGuidance> =
        withContext(dispatchers.io) {
            when (val gatewayResult = guidanceGateway.deriveGuidance(profile)) {
                is AppResult.Success -> {
                    val guidance = NutritionGuidance(
                        userId = profile.id,
                        calorieTarget = gatewayResult.value.calorieTarget,
                        suggestedProteinRange = gatewayResult.value.suggestedProteinRange,
                        suggestedCarbRange = gatewayResult.value.suggestedCarbRange,
                        suggestedFatRange = gatewayResult.value.suggestedFatRange,
                        derivationExplanation = gatewayResult.value.derivationExplanation,
                        notes = gatewayResult.value.notes,
                        generatedAt = timeProvider.now(),
                    )

                    runCatching {
                        guidanceDao.upsert(guidance.toEntity())
                    }.fold(
                        onSuccess = { AppResult.Success(guidance) },
                        onFailure = { throwable ->
                            AppResult.Failure(
                                AppError.Storage(
                                    message = throwable.message
                                        ?: "Failed to store nutrition guidance.",
                                ),
                            )
                        },
                    )
                }
                is AppResult.Failure -> gatewayResult
            }
        }
}
