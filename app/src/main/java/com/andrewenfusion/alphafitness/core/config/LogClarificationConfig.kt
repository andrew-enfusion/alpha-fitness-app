package com.andrewenfusion.alphafitness.core.config

import javax.inject.Inject

class LogClarificationConfig @Inject constructor() {
    val lowConfidenceThreshold: Float = 0.6f
    val maxClarificationRounds: Int = 1
}
