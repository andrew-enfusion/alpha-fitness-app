package com.andrewenfusion.alphafitness.data.gateway.onboarding

import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.config.OpenAiOnboardingConfig
import com.andrewenfusion.alphafitness.data.gateway.onboarding.model.OnboardingGuidanceAiContract
import com.andrewenfusion.alphafitness.data.gateway.onboarding.model.OpenAiResponsesFormatConfig
import com.andrewenfusion.alphafitness.data.gateway.onboarding.model.OpenAiResponsesRequest
import com.andrewenfusion.alphafitness.data.gateway.onboarding.model.OpenAiResponsesTextConfig
import com.andrewenfusion.alphafitness.domain.model.NutritionGuidanceDraft
import com.andrewenfusion.alphafitness.domain.model.UserProfile
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
class OpenAiOnboardingGuidanceGateway @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json,
    private val config: OpenAiOnboardingConfig,
    private val promptFactory: OpenAiOnboardingPromptFactory,
    private val validator: OnboardingGuidanceValidator,
) {
    suspend fun deriveGuidance(profile: UserProfile): AppResult<NutritionGuidanceDraft> {
        if (!config.isConfigured) {
            return AppResult.Failure(AppError.AiUnavailable())
        }

        val requestBody = OpenAiResponsesRequest(
            model = config.model,
            instructions = promptFactory.buildInstructions(),
            input = promptFactory.buildInput(profile),
            text = OpenAiResponsesTextConfig(
                format = OpenAiResponsesFormatConfig(
                    schemaName = "onboarding_guidance",
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
            val draft = validator.toDraftOrNull(contract, profile)
                ?: return AppResult.Failure(AppError.AiMalformedResponse())
            return AppResult.Success(draft)
        }
    }

    private fun parseStructuredOutput(rawResponse: String): OnboardingGuidanceAiContract? =
        try {
            val root = json.parseToJsonElement(rawResponse).jsonObject
            val outputArray = root["output"]?.jsonArray ?: return null
            val firstMessage = outputArray.firstOrNull()?.jsonObject ?: return null
            val contentArray = firstMessage["content"]?.jsonArray ?: return null
            val outputText = contentArray.firstOrNull { item ->
                item.jsonObject["type"]?.jsonPrimitive?.content == "output_text"
            }?.jsonObject ?: return null
            val rawText = outputText["text"]?.jsonPrimitive?.content ?: return null
            json.decodeFromString(OnboardingGuidanceAiContract.serializer(), rawText)
        } catch (_: IllegalArgumentException) {
            null
        } catch (_: SerializationException) {
            null
        }

    private companion object {
        val JSON_MEDIA_TYPE = "application/json".toMediaType()
    }
}
