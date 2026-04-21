package com.andrewenfusion.alphafitness.feature.log

sealed interface LogSaveState {
    data object Idle : LogSaveState

    data object Saving : LogSaveState

    data class Success(
        val savedMealId: String,
        val warningMessage: String? = null,
    ) : LogSaveState

    data class Failure(
        val message: String,
    ) : LogSaveState
}
