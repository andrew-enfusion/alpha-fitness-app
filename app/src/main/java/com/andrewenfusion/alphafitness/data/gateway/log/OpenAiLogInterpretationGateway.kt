package com.andrewenfusion.alphafitness.data.gateway.log

import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.config.OpenAiLogConfig
import com.andrewenfusion.alphafitness.data.gateway.log.model.LogMealAiContract
import com.andrewenfusion.alphafitness.data.gateway.onboarding.model.OpenAiResponsesFormatConfig
import com.andrewenfusion.alphafitness.data.gateway.onboarding.model.OpenAiResponsesRequest
import com.andrewenfusion.alphafitness.data.gateway.onboarding.model.OpenAiResponsesTextConfig
import com.andrewenfusion.alphafitness.domain.model.LogMealInterpretationDraft
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class OpenAiLogInterpretationGateway @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json,
    private val config: OpenAiLogConfig,
    private val promptFactory: OpenAiLogPromptFactory,
    private val validator: LogInterpretationValidator,
    private val developmentGateway: DevelopmentLogInterpretationGateway,
) : LogInterpretationGateway {
    override suspend fun interpretMealDescription(
        description: String,
    ): AppResult<LogMealInterpretationDraft> {
        if (!config.isConfigured) {
            return developmentGateway.interpretMealDescription(description)
        }

        val requestBody = OpenAiResponsesRequest(
            model = config.model,
            instructions = promptFactory.buildInstructions(),
            input = promptFactory.buildInput(description),
            text = OpenAiResponsesTextConfig(
                format = OpenAiResponsesFormatConfig(
                    schemaName = "log_meal",
                    jsonSchema = promptFactory.buildJsonSchema(),
                ),
            ),
        )

        val request = Request.Builder()
            .url(config.responsesUrl)
            .header("Authorization", "Bearer ${config.apiKey}")
            .header("Content-Type", "application/json")
            .post(
                json.encodeToString(OpenAiResponsesRequest.serializer(), requestBody)
                    .toRequestBody(JSON_MEDIA_TYPE),
            )
            .build()

        val response = try {
            client.newCall(request).execute()
        } catch (throwable: Throwable) {
            return when (throwable) {
                is SocketTimeoutException -> AppResult.Failure(AppError.AiTimeout())
                is IOException -> AppResult.Failure(AppError.NetworkUnavailable())
                else -> AppResult.Failure(AppError.AiUnavailable())
            }
        }

        response.use { handled ->
            if (!handled.isSuccessful) {
                return AppResult.Failure(AppError.AiUnavailable())
            }

            val body = handled.body?.string().orEmpty()
            val contract = parseStructuredOutput(body)
                ?: return AppResult.Failure(AppError.AiMalformedResponse())
            val draft = validator.toDraftOrNull(contract)
                ?: return AppResult.Failure(AppError.AiMalformedResponse())
            return AppResult.Success(draft)
        }
    }

    private fun parseStructuredOutput(
        rawResponse: String,
    ): LogMealAiContract? =
        try {
            val root = json.parseToJsonElement(rawResponse).jsonObject
            val outputArray = root["output"]?.jsonArray ?: return null
            val firstMessage = outputArray.firstOrNull()?.jsonObject ?: return null
            val contentArray = firstMessage["content"]?.jsonArray ?: return null
            val outputText = contentArray.firstOrNull { item ->
                item.jsonObject["type"]?.jsonPrimitive?.content == "output_text"
            }?.jsonObject ?: return null
            val rawText = outputText["text"]?.jsonPrimitive?.content ?: return null
            json.decodeFromString(LogMealAiContract.serializer(), rawText)
        } catch (_: IllegalArgumentException) {
            null
        } catch (_: SerializationException) {
            null
        }

    private companion object {
        val JSON_MEDIA_TYPE = "application/json".toMediaType()
    }
}
