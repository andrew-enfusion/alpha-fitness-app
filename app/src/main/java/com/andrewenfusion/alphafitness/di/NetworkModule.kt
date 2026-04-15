package com.andrewenfusion.alphafitness.di

import com.andrewenfusion.alphafitness.core.config.OpenAiOnboardingConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        config: OpenAiOnboardingConfig,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .callTimeout(config.timeoutSeconds, TimeUnit.SECONDS)
            .connectTimeout(config.timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.timeoutSeconds, TimeUnit.SECONDS)
            .build()
}
