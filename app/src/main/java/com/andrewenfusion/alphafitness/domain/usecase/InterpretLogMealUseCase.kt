package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.repository.MealRepository
import javax.inject.Inject

class InterpretLogMealUseCase @Inject constructor(
    private val repository: MealRepository,
) {
    suspend operator fun invoke(
        submittedText: String,
    ): AppResult<LogMealReviewState> {
        val normalizedText = submittedText.trim()

        if (normalizedText.isBlank()) {
            return AppResult.Failure(
                error = AppError.Validation(
                    message = "Enter a meal description before interpreting it.",
                ),
            )
        }

        return when (val localResult = repository.lookupLocalInterpretation(normalizedText)) {
            is AppResult.Success -> {
                val localReview = localResult.value
                if (localReview != null) {
                    AppResult.Success(
                        localReview.copy(
                            interpretationSource = LogMealInterpretationSource.LOCAL_MATCH,
                        ),
                    )
                } else {
                    when (val remoteResult = repository.interpretWithGateway(normalizedText)) {
                        is AppResult.Success -> {
                            AppResult.Success(
                                remoteResult.value.copy(
                                    interpretationSource = LogMealInterpretationSource.AI_FALLBACK,
                                ),
                            )
                        }
                        is AppResult.Failure -> remoteResult
                    }
                }
            }
            is AppResult.Failure -> localResult
        }
    }
}
