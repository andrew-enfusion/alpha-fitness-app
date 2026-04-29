package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.config.LogClarificationConfig
import com.andrewenfusion.alphafitness.core.common.time.TimeProvider
import com.andrewenfusion.alphafitness.core.config.LocalMealMemoryConfig
import com.andrewenfusion.alphafitness.data.gateway.log.LogInterpretationGateway
import com.andrewenfusion.alphafitness.domain.engine.LocalMealMemoryMatcher
import com.andrewenfusion.alphafitness.domain.model.LogClarificationOption
import com.andrewenfusion.alphafitness.domain.model.LogClarificationState
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationDraft
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.repository.MealRepository
import com.andrewenfusion.alphafitness.feature.log.LogOutputState
import javax.inject.Inject

class InterpretLogMealUseCase @Inject constructor(
    private val repository: MealRepository,
    private val logInterpretationGateway: LogInterpretationGateway,
    private val localMealMemoryMatcher: LocalMealMemoryMatcher,
    private val localMealMemoryConfig: LocalMealMemoryConfig,
    private val logClarificationConfig: LogClarificationConfig,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(
        originalSubmittedDraft: String,
        clarificationAnswer: String? = null,
    ): AppResult<LogOutputState> {
        val normalizedOriginalDraft = originalSubmittedDraft.trim()
        val normalizedClarificationAnswer = clarificationAnswer?.trim()?.takeIf { it.isNotBlank() }

        if (normalizedOriginalDraft.isBlank()) {
            return AppResult.Failure(
                error = AppError.Validation(
                    message = "Enter a meal description before interpreting it.",
                ),
            )
        }

        val interpretationInput = buildInterpretationInput(
            originalSubmittedDraft = normalizedOriginalDraft,
            clarificationAnswer = normalizedClarificationAnswer,
        )

        return when (val localResult = repository.getRecentSavedMeals(localMealMemoryConfig.recentMealCandidateLimit)) {
            is AppResult.Success -> {
                val localReview = localMealMemoryMatcher.match(
                    submittedText = interpretationInput,
                    recentSavedMeals = localResult.value,
                    now = timeProvider.now(),
                )
                if (localReview != null) {
                    AppResult.Success(
                        LogOutputState.ReviewReady(
                            reviewState = localReview.copy(
                                submittedText = normalizedOriginalDraft,
                                assumptions = localReview.assumptions.withClarificationContext(normalizedClarificationAnswer),
                                interpretationSource = LogMealInterpretationSource.LOCAL_MATCH,
                            ),
                        ),
                    )
                } else {
                    when (val remoteResult = logInterpretationGateway.interpretMealDescription(interpretationInput)) {
                        is AppResult.Success -> {
                            val remoteDraft = remoteResult.value.copy(
                                reviewState = remoteResult.value.reviewState.copy(
                                submittedText = normalizedOriginalDraft,
                                    assumptions = remoteResult.value.reviewState.assumptions
                                        .withClarificationContext(normalizedClarificationAnswer),
                                    interpretationSource = LogMealInterpretationSource.AI_FALLBACK,
                                ),
                            )
                            mapRemoteDraftToOutputState(
                                originalSubmittedDraft = normalizedOriginalDraft,
                                clarificationAnswer = normalizedClarificationAnswer,
                                remoteDraft = remoteDraft,
                            )
                        }
                        is AppResult.Failure -> remoteResult
                    }
                }
            }
            is AppResult.Failure -> localResult
        }
    }

    private fun mapRemoteDraftToOutputState(
        originalSubmittedDraft: String,
        clarificationAnswer: String?,
        remoteDraft: LogMealInterpretationDraft,
    ): AppResult<LogOutputState> {
        val remoteReview = remoteDraft.reviewState
        val needsClarification = clarificationAnswer == null &&
            shouldRequestClarification(remoteDraft)

        if (needsClarification) {
            return AppResult.Success(
                LogOutputState.LowConfidence(
                    clarificationState = LogClarificationState(
                        originalSubmittedDraft = originalSubmittedDraft,
                        question = remoteDraft.clarificationQuestion ?: buildClarificationQuestion(originalSubmittedDraft),
                        quickOptions = buildQuickOptions(originalSubmittedDraft),
                        partialReviewState = remoteReview,
                    ),
                ),
            )
        }

        if (clarificationAnswer != null &&
            (remoteDraft.clarificationNeeded || remoteReview.items.isEmpty()) &&
            logClarificationConfig.maxClarificationRounds == 1
        ) {
            return AppResult.Failure(
                AppError.Unknown(
                    message = "I still couldn't build a clear draft. Try a more specific meal description.",
                ),
            )
        }

        return AppResult.Success(
            LogOutputState.ReviewReady(
                reviewState = remoteReview.copy(
                    assumptions = remoteReview.assumptions.withBestEffortClarificationFallback(
                        clarificationAnswer = clarificationAnswer,
                        confidenceThreshold = logClarificationConfig.lowConfidenceThreshold,
                        confidence = remoteReview.confidence,
                    ),
                ),
            ),
        )
    }

    private fun shouldRequestClarification(
        remoteDraft: LogMealInterpretationDraft,
    ): Boolean =
        remoteDraft.clarificationNeeded ||
            remoteDraft.reviewState.confidence < logClarificationConfig.lowConfidenceThreshold

    private fun buildInterpretationInput(
        originalSubmittedDraft: String,
        clarificationAnswer: String?,
    ): String =
        if (clarificationAnswer.isNullOrBlank()) {
            originalSubmittedDraft
        } else {
            "$clarificationAnswer $originalSubmittedDraft"
        }

    private fun buildClarificationQuestion(
        originalSubmittedDraft: String,
    ): String {
        val normalizedDraft = originalSubmittedDraft.lowercase()
        return when {
            drinkTerms.any { it in normalizedDraft } -> {
                "What kind of drink was it?"
            }
            proteinOrStyleTerms.any { it in normalizedDraft } -> {
                "What was the main protein or style?"
            }
            else -> {
                "What was the main item in this meal?"
            }
        }
    }

    private fun buildQuickOptions(
        originalSubmittedDraft: String,
    ): List<LogClarificationOption> {
        val normalizedDraft = originalSubmittedDraft.lowercase()
        return if (drinkTerms.any { it in normalizedDraft }) {
            listOf(
                LogClarificationOption(label = "Diet", value = "Diet soda"),
                LogClarificationOption(label = "Regular", value = "Regular soda"),
                LogClarificationOption(label = "Coffee", value = "Coffee drink"),
            )
        } else {
            listOf(
                LogClarificationOption(label = "Chicken", value = "Chicken"),
                LogClarificationOption(label = "Beef", value = "Beef"),
                LogClarificationOption(label = "Vegetarian", value = "Vegetarian"),
            )
        }
    }

    private fun List<String>.withClarificationContext(
        clarificationAnswer: String?,
    ): List<String> =
        if (clarificationAnswer.isNullOrBlank()) {
            this
        } else {
            this + "Clarification used: $clarificationAnswer."
        }

    private fun List<String>.withBestEffortClarificationFallback(
        clarificationAnswer: String?,
        confidenceThreshold: Float,
        confidence: Float,
    ): List<String> =
        if (
            clarificationAnswer.isNullOrBlank() ||
            confidence >= confidenceThreshold
        ) {
            this
        } else {
            this + "Clarification was limited, so this draft remains a best-effort estimate."
        }
    private companion object {
        val drinkTerms = setOf(
            "drink",
            "beverage",
            "soda",
            "pop",
            "coffee",
            "tea",
        )

        val proteinOrStyleTerms = setOf(
            "sandwich",
            "wrap",
            "burger",
            "burrito",
            "pasta",
            "salad",
            "bowl",
        )
    }
}
