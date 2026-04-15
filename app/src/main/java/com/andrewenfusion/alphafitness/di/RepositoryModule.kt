package com.andrewenfusion.alphafitness.di

import com.andrewenfusion.alphafitness.data.gateway.onboarding.DevelopmentOnboardingGuidanceGateway
import com.andrewenfusion.alphafitness.data.gateway.onboarding.OnboardingGuidanceGateway
import com.andrewenfusion.alphafitness.data.repository.RoomNutritionGuidanceRepository
import com.andrewenfusion.alphafitness.data.repository.RoomUserProfileRepository
import com.andrewenfusion.alphafitness.domain.repository.NutritionGuidanceRepository
import com.andrewenfusion.alphafitness.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindOnboardingGuidanceGateway(
        gateway: DevelopmentOnboardingGuidanceGateway,
    ): OnboardingGuidanceGateway

    @Binds
    @Singleton
    abstract fun bindNutritionGuidanceRepository(
        repository: RoomNutritionGuidanceRepository,
    ): NutritionGuidanceRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        repository: RoomUserProfileRepository,
    ): UserProfileRepository
}
