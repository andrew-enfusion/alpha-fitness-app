package com.andrewenfusion.alphafitness.core.common.error

sealed interface AppError {
    val recoverable: Boolean
    val message: String

    data class Storage(
        override val message: String,
        override val recoverable: Boolean = true,
    ) : AppError

    data class Validation(
        override val message: String,
        override val recoverable: Boolean = true,
    ) : AppError

    data class Unsupported(
        override val message: String,
        override val recoverable: Boolean = false,
    ) : AppError

    data class Unknown(
        override val message: String,
        override val recoverable: Boolean = true,
    ) : AppError
}
