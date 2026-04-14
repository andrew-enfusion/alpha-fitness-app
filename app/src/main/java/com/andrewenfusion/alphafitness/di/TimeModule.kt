package com.andrewenfusion.alphafitness.di

import com.andrewenfusion.alphafitness.core.common.time.SystemTimeProvider
import com.andrewenfusion.alphafitness.core.common.time.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {
    @Binds
    @Singleton
    abstract fun bindTimeProvider(
        provider: SystemTimeProvider,
    ): TimeProvider
}
