package com.andrewenfusion.alphafitness.core.common.result

import com.andrewenfusion.alphafitness.core.common.error.AppError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultTest {
    @Test
    fun mapTransformsSuccessValues() {
        val result = AppResult.Success(21)

        val mapped = result.map { it * 2 }

        assertEquals(AppResult.Success(42), mapped)
    }

    @Test
    fun mapPreservesFailure() {
        val result: AppResult<Int> = AppResult.Failure(AppError.Validation("Invalid"))

        val mapped = result.map { it * 2 }

        assertTrue(mapped is AppResult.Failure)
    }
}
