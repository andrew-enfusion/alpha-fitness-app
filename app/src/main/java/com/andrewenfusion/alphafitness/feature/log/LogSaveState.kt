package com.andrewenfusion.alphafitness.feature.log

sealed interface LogSaveState {
    data object Idle : LogSaveState

    data object Saving : LogSaveState

    data class Success(
        val savedMealId: String,
    ) : LogSaveState

    data class Error(
        val message: String,
    ) : LogSaveState
}
