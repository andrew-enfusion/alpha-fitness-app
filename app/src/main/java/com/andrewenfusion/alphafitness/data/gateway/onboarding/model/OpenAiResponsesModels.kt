package com.andrewenfusion.alphafitness.data.gateway.onboarding.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OpenAiResponsesRequest(
    val model: String,
    val instructions: String,
    val input: String,
    val store: Boolean = false,
    val text: OpenAiResponsesTextConfig,
)

@Serializable
data class OpenAiResponsesTextConfig(
    val format: OpenAiResponsesFormatConfig,
)

@Serializable
data class OpenAiResponsesFormatConfig(
    val type: String = "json_schema",
    @SerialName("name") val schemaName: String,
    @SerialName("schema") val jsonSchema: JsonElement,
    val strict: Boolean = true,
)
