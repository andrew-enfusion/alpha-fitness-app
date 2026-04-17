package com.andrewenfusion.alphafitness.feature.profile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val age: String = "--",
    val sex: String = "--",
    val height: String = "--",
    val weight: String = "--",
    val exerciseLevel: String = "--",
    val jobActivityLevel: String = "--",
    val goalType: String = "--",
    val baselineTarget: String = "--",
    val workingTarget: String = "--",
    val proteinRange: String? = null,
    val carbRange: String? = null,
    val fatRange: String? = null,
    val explanation: String? = null,
)
