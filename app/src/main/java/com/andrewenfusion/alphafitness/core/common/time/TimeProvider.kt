package com.andrewenfusion.alphafitness.core.common.time

import java.time.Instant

interface TimeProvider {
    fun now(): Instant
}
