package com.andrewenfusion.alphafitness.domain.model

import java.time.Instant

data class SavedMealMemory(
    val mealEntryId: String,
    val rawInput: String?,
    val timestamp: Instant,
    val items: List<SavedMealMemoryItem>,
)
