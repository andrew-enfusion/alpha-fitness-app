package com.andrewenfusion.alphafitness.data.repository

import com.andrewenfusion.alphafitness.core.common.dispatcher.AppDispatchers
import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.database.dao.DailyMetricsDao
import com.andrewenfusion.alphafitness.data.mapper.toEntity
import com.andrewenfusion.alphafitness.domain.model.DailyMetrics
import com.andrewenfusion.alphafitness.domain.repository.DailyMetricsRepository
import javax.inject.Inject
import kotlinx.coroutines.withContext

class RoomDailyMetricsRepository @Inject constructor(
    private val dailyMetricsDao: DailyMetricsDao,
    private val dispatchers: AppDispatchers,
) : DailyMetricsRepository {
    override suspend fun replaceDailyMetrics(metrics: DailyMetrics): AppResult<Unit> =
        withContext(dispatchers.io) {
            runCatching {
                dailyMetricsDao.upsert(metrics.toEntity())
            }.fold(
                onSuccess = { AppResult.Success(Unit) },
                onFailure = { throwable ->
                    AppResult.Failure(
                        AppError.Storage(
                            message = throwable.message ?: "Failed to refresh daily totals.",
                        ),
                    )
                },
            )
        }
}
