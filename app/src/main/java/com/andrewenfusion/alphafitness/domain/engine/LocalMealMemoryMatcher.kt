package com.andrewenfusion.alphafitness.domain.engine

import com.andrewenfusion.alphafitness.core.config.LocalMealMemoryConfig
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationSource
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewItem
import com.andrewenfusion.alphafitness.domain.model.LogMealReviewState
import com.andrewenfusion.alphafitness.domain.model.SavedMealMemory
import com.andrewenfusion.alphafitness.domain.model.SavedMealMemoryItem
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.math.max

class LocalMealMemoryMatcher @Inject constructor(
    private val config: LocalMealMemoryConfig,
) {
    fun match(
        submittedText: String,
        recentSavedMeals: List<SavedMealMemory>,
        now: Instant,
    ): LogMealReviewState? {
        val normalizedInput = normalize(submittedText)
        val inputTokens = tokenize(normalizedInput)
        if (inputTokens.isEmpty()) {
            return null
        }

        val bestCandidate = recentSavedMeals
            .asSequence()
            .mapNotNull { meal ->
                val matchedItems = meal.items
                    .mapNotNull { item ->
                        val itemScore = scoreItem(
                            normalizedInput = normalizedInput,
                            inputTokens = inputTokens,
                            item = item,
                        )
                        if (itemScore >= config.minimumItemScore) {
                            MatchedItem(
                                item = item,
                                score = itemScore,
                            )
                        } else {
                            null
                        }
                    }

                if (matchedItems.isEmpty()) {
                    return@mapNotNull null
                }

                val mealScore = scoreMeal(
                    normalizedInput = normalizedInput,
                    inputTokens = inputTokens,
                    meal = meal,
                    matchedItems = matchedItems,
                    now = now,
                )

                if (mealScore < config.minimumMealScore) {
                    null
                } else {
                    CandidateMatch(
                        meal = meal,
                        matchedItems = matchedItems,
                        mealScore = mealScore,
                    )
                }
            }
            .maxByOrNull { candidate -> candidate.mealScore }

        return bestCandidate?.toReviewState(submittedText)
    }

    private fun scoreMeal(
        normalizedInput: String,
        inputTokens: Set<String>,
        meal: SavedMealMemory,
        matchedItems: List<MatchedItem>,
        now: Instant,
    ): Float {
        val mealTokens = tokenize(
            buildString {
                append(meal.rawInput.orEmpty())
                append(' ')
                meal.items.forEach { item ->
                    append(item.displayName)
                    append(' ')
                    append(item.portionDescription.orEmpty())
                    append(' ')
                }
            },
        )
        val overlapRatio = overlapRatio(inputTokens, mealTokens)
        val phraseBoost = phraseContainmentBoost(normalizedInput, meal.rawInput.orEmpty())
        val itemCoverageBoost = matchedItems.size.toFloat() / meal.items.size.coerceAtLeast(1)
        val recencyBoost = recencyBoost(
            timestamp = meal.timestamp,
            now = now,
        )

        return overlapRatio + phraseBoost + itemCoverageBoost + recencyBoost
    }

    private fun scoreItem(
        normalizedInput: String,
        inputTokens: Set<String>,
        item: SavedMealMemoryItem,
    ): Float {
        val itemTokens = tokenize(item.displayName)
        val overlapRatio = overlapRatio(inputTokens, itemTokens)
        val phraseBoost = max(
            phraseContainmentBoost(normalizedInput, item.displayName),
            phraseContainmentBoost(normalizedInput, item.portionDescription.orEmpty()),
        )

        return overlapRatio + phraseBoost
    }

    private fun recencyBoost(
        timestamp: Instant,
        now: Instant,
    ): Float {
        val ageInDays = max(
            Duration.between(timestamp, now).toDays(),
            0L,
        )

        return when {
            ageInDays <= config.recentMealBoostDays -> 0.4f
            ageInDays <= config.warmMealBoostDays -> 0.2f
            else -> 0f
        }
    }

    private fun overlapRatio(
        left: Set<String>,
        right: Set<String>,
    ): Float {
        if (left.isEmpty() || right.isEmpty()) {
            return 0f
        }

        val sharedCount = left.intersect(right).size.toFloat()
        return sharedCount / left.size.toFloat()
    }

    private fun phraseContainmentBoost(
        normalizedInput: String,
        candidatePhrase: String,
    ): Float {
        val normalizedCandidate = normalize(candidatePhrase)
        if (normalizedCandidate.isBlank()) {
            return 0f
        }

        return when {
            normalizedInput == normalizedCandidate -> 1.1f
            normalizedInput.contains(normalizedCandidate) -> 0.8f
            normalizedCandidate.contains(normalizedInput) -> 0.6f
            else -> 0f
        }
    }

    private fun normalize(
        value: String,
    ): String =
        value
            .lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    private fun tokenize(
        value: String,
    ): Set<String> =
        normalize(value)
            .split(' ')
            .filter { token ->
                token.length > 1 && token !in STOP_WORDS
            }
            .toSet()

    private fun CandidateMatch.toReviewState(
        submittedText: String,
    ): LogMealReviewState {
        val reviewItems = matchedItems.map { matchedItem ->
            val item = matchedItem.item
            LogMealReviewItem(
                displayName = item.displayName,
                portionDescription = item.portionDescription ?: "Saved serving",
                calories = item.calories,
                protein = item.protein,
                carbs = item.carbs,
                fat = item.fat,
                assumptions = item.assumptions,
                confidence = matchedItem.score.toConfidenceBand(),
            )
        }
        val assumptions = buildList {
            add("Matched against a recent confirmed meal already saved on this device.")
            if (reviewItems.size < meal.items.size) {
                add("Only the matched items from that saved meal were reused in this draft.")
            }
            addAll(
                reviewItems
                    .map { item -> item.assumptions }
                    .filter { assumption -> assumption.isNotBlank() }
                    .distinct(),
            )
        }

        return LogMealReviewState(
            submittedText = submittedText,
            interpretationSource = LogMealInterpretationSource.LOCAL_MATCH,
            items = reviewItems,
            assumptions = assumptions,
            requiresReview = true,
            confidence = mealScore.toConfidenceBand(),
        )
    }

    private fun Float.toConfidenceBand(): Float =
        when {
            this >= 3.2f -> 0.88f
            this >= 2.7f -> 0.8f
            else -> 0.72f
        }

    private data class CandidateMatch(
        val meal: SavedMealMemory,
        val matchedItems: List<MatchedItem>,
        val mealScore: Float,
    )

    private data class MatchedItem(
        val item: SavedMealMemoryItem,
        val score: Float,
    )

    private companion object {
        val STOP_WORDS: Set<String> = setOf(
            "a",
            "an",
            "and",
            "for",
            "i",
            "my",
            "of",
            "the",
            "to",
            "with",
        )
    }
}
