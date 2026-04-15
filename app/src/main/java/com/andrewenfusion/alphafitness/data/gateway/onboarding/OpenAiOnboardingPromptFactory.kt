package com.andrewenfusion.alphafitness.data.gateway.onboarding

import com.andrewenfusion.alphafitness.domain.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Singleton
class OpenAiOnboardingPromptFactory @Inject constructor() {
    fun buildInstructions(): String =
        """
        You derive onboarding calorie guidance for a local-first Android calorie tracking app.
        Return strict JSON only.
        Follow these rules:
        - contractVersion must be 1
        - actionType must be DERIVE_ONBOARDING_GUIDANCE
        - requiresReview must be false
        - UserProfile.calorieTarget is the deterministic baseline and must never be overwritten
        - workingCalorieTarget is the AI-adjusted working target used by the app
        - when no adjustment is needed, workingCalorieTarget must equal the baseline and calorieAdjustment must be 0
        - macros are guidance only, not user goals
        - do not ask for grams or precise measurements
        - keep derivationExplanation concise, plain, and user-facing
        - notes should mention caveats briefly when relevant
        """.trimIndent()

    fun buildInput(profile: UserProfile): String =
        """
        Derive onboarding guidance from this user profile only.
        Profile:
        - age: ${profile.age}
        - sex: ${profile.sex.name}
        - heightCm: ${profile.heightCm}
        - weightKg: ${profile.weightKg}
        - exerciseLevel: ${profile.exerciseLevel.name}
        - jobActivityLevel: ${profile.jobActivityLevel.name}
        - goalType: ${profile.goalType.name}
        - deterministicBaselineCalorieTarget: ${profile.calorieTarget}
        """.trimIndent()

    fun buildJsonSchema(): JsonObject =
        buildJsonObject {
            put("type", "object")
            put(
                "properties",
                buildJsonObject {
                    put("contractVersion", integerSchema())
                    put("actionType", stringSchema())
                    put("confidence", numberSchema())
                    put("rationale", stringSchema())
                    put("requiresReview", booleanSchema())
                    put("workingCalorieTarget", integerSchema())
                    put("calorieAdjustment", integerSchema())
                    put("suggestedProteinRange", stringSchema())
                    put("suggestedCarbRange", stringSchema())
                    put("suggestedFatRange", stringSchema())
                    put("derivationExplanation", stringSchema())
                    put("notes", stringSchema())
                },
            )
            put(
                "required",
                JsonArray(
                    listOf(
                        JsonPrimitive("contractVersion"),
                        JsonPrimitive("actionType"),
                        JsonPrimitive("confidence"),
                        JsonPrimitive("rationale"),
                        JsonPrimitive("requiresReview"),
                        JsonPrimitive("workingCalorieTarget"),
                        JsonPrimitive("calorieAdjustment"),
                        JsonPrimitive("suggestedProteinRange"),
                        JsonPrimitive("suggestedCarbRange"),
                        JsonPrimitive("suggestedFatRange"),
                        JsonPrimitive("derivationExplanation"),
                    ),
                ),
            )
            put("additionalProperties", JsonPrimitive(false))
        }

    private fun stringSchema(): JsonObject =
        buildJsonObject { put("type", "string") }

    private fun integerSchema(): JsonObject =
        buildJsonObject { put("type", "integer") }

    private fun numberSchema(): JsonObject =
        buildJsonObject { put("type", "number") }

    private fun booleanSchema(): JsonObject =
        buildJsonObject { put("type", "boolean") }
}
