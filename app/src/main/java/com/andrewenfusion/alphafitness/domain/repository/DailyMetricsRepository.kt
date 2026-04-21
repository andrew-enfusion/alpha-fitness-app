package com.andrewenfusion.alphafitness.domain.repository

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.DailyMetrics

interface DailyMetricsRepository {
    suspend fun replaceDailyMetrics(metrics: DailyMetrics): AppResult<Unit>
}
