package com.andrewenfusion.alphafitness.core.config

import com.andrewenfusion.alphafitness.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAiLogConfig @Inject constructor() {
    val apiKey: String = BuildConfig.OPENAI_API_KEY
    val model: String = BuildConfig.OPENAI_MODEL
    val responsesUrl: String = BuildConfig.OPENAI_RESPONSES_URL
    val timeoutSeconds: Long = BuildConfig.OPENAI_TIMEOUT_SECONDS.toLong()

    val isConfigured: Boolean
        get() = apiKey.isNotBlank()
}
