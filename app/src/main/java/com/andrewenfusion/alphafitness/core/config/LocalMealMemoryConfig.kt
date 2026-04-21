package com.andrewenfusion.alphafitness.core.config

import javax.inject.Inject

class LocalMealMemoryConfig @Inject constructor() {
    val recentMealCandidateLimit: Int = 30
    val minimumItemScore: Float = 0.5f
    val minimumMealScore: Float = 2.0f
    val recentMealBoostDays: Long = 7L
    val warmMealBoostDays: Long = 30L
}
