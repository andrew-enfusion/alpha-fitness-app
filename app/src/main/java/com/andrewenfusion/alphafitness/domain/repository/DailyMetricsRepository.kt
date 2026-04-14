package com.andrewenfusion.alphafitness.domain.repository

import com.andrewenfusion.alphafitness.domain.model.DailyMetrics
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface DailyMetricsRepository {
    fun observeDailyMetrics(date: LocalDate): Flow<DailyMetrics?>
}
