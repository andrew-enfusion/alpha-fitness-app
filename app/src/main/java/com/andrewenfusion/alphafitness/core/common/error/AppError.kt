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

    data class NetworkUnavailable(
        override val message: String = AiFailureMessages.OFFLINE_OR_NETWORK_REQUIRED,
        override val recoverable: Boolean = true,
    ) : AppError

    data class AiTimeout(
        override val message: String = AiFailureMessages.TIMEOUT_OR_UNKNOWN,
        override val recoverable: Boolean = true,
    ) : AppError

    data class AiUnavailable(
        override val message: String = AiFailureMessages.TIMEOUT_OR_UNKNOWN,
        override val recoverable: Boolean = true,
    ) : AppError

    data class AiMalformedResponse(
        override val message: String = AiFailureMessages.MALFORMED_RESPONSE,
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
