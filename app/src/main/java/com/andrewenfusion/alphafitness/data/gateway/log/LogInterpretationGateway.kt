package com.andrewenfusion.alphafitness.data.gateway.log

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationDraft

interface LogInterpretationGateway {
    suspend fun interpretMealDescription(
        description: String,
    ): AppResult<LogMealInterpretationDraft>
}
