package com.andrewenfusion.alphafitness.data.gateway.log

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

@Singleton
class OpenAiLogPromptFactory @Inject constructor() {
    fun buildInstructions(): String =
        """
        You are generating a strict JSON response for Alpha Fitness App meal logging.
        Return only a LOG_MEAL contract as valid JSON matching the provided schema.
        Use reasonable calorie and macro assumptions when exact preparation is unknown.
        If the meal description is too vague for a confident review draft, set clarificationNeeded to true and include one short clarificationQuestion.
        Never ask for grams, exact weights, or precise measurements.
        """.trimIndent()

    fun buildInput(
        description: String,
    ): String =
        "Interpret this meal description for calorie tracking: $description"

    fun buildJsonSchema(): JsonElement =
        buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("contractVersion") {
                    put("type", "integer")
                    put("const", 1)
                }
                putJsonObject("actionType") {
                    put("type", "string")
                    put("const", "LOG_MEAL")
                }
                putJsonObject("confidence") {
                    put("type", "number")
                }
                putJsonObject("rationale") {
                    put("type", "string")
                }
                putJsonObject("requiresReview") {
                    put("type", "boolean")
                }
                putJsonObject("mealItems") {
                    put("type", "array")
                    putJsonObject("items") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("name") { put("type", "string") }
                            putJsonObject("quantity") { put("type", "number") }
                            putJsonObject("unit") { put("type", "string") }
                            putJsonObject("portionDescription") { put("type", "string") }
                            putJsonObject("preparationType") { put("type", "string") }
                            putJsonObject("contextHints") {
                                put("type", "array")
                                putJsonObject("items") { put("type", "string") }
                            }
                            putJsonObject("calories") { put("type", "integer") }
                            putJsonObject("protein") { put("type", "number") }
                            putJsonObject("carbs") { put("type", "number") }
                            putJsonObject("fat") { put("type", "number") }
                            putJsonObject("confidence") { put("type", "number") }
                        }
                        put(
                            "required",
                            JsonArray(
                                listOf(
                                    JsonPrimitive("name"),
                                    JsonPrimitive("contextHints"),
                                    JsonPrimitive("calories"),
                                    JsonPrimitive("protein"),
                                    JsonPrimitive("carbs"),
                                    JsonPrimitive("fat"),
                                    JsonPrimitive("confidence"),
                                ),
                            ),
                        )
                        put("additionalProperties", false)
                    }
                }
                putJsonObject("clarificationNeeded") {
                    put("type", "boolean")
                }
                putJsonObject("clarificationQuestion") {
                    put("type", "string")
                }
                putJsonObject("mealTimeHint") {
                    put("type", "string")
                }
                putJsonObject("sourceAssumptions") {
                    put("type", "array")
                    putJsonObject("items") { put("type", "string") }
                }
            }
            put(
                "required",
                buildJsonArray {
                    add(JsonPrimitive("contractVersion"))
                    add(JsonPrimitive("actionType"))
                    add(JsonPrimitive("confidence"))
                    add(JsonPrimitive("rationale"))
                    add(JsonPrimitive("requiresReview"))
                    add(JsonPrimitive("mealItems"))
                    add(JsonPrimitive("clarificationNeeded"))
                    add(JsonPrimitive("sourceAssumptions"))
                },
            )
            put("additionalProperties", false)
        }
}
