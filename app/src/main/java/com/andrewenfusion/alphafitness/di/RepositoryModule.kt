package com.andrewenfusion.alphafitness.di

import com.andrewenfusion.alphafitness.data.gateway.onboarding.ConfiguredOnboardingGuidanceGateway
import com.andrewenfusion.alphafitness.data.gateway.onboarding.OnboardingGuidanceGateway
import com.andrewenfusion.alphafitness.data.gateway.log.DevelopmentLogInterpretationGateway
import com.andrewenfusion.alphafitness.data.gateway.log.LogInterpretationGateway
import com.andrewenfusion.alphafitness.data.repository.RoomMealRepository
import com.andrewenfusion.alphafitness.data.repository.RoomNutritionGuidanceRepository
import com.andrewenfusion.alphafitness.data.repository.RoomUserProfileRepository
import com.andrewenfusion.alphafitness.domain.repository.MealRepository
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
    abstract fun bindLogInterpretationGateway(
        gateway: DevelopmentLogInterpretationGateway,
    ): LogInterpretationGateway

    @Binds
    @Singleton
    abstract fun bindOnboardingGuidanceGateway(
        gateway: ConfiguredOnboardingGuidanceGateway,
    ): OnboardingGuidanceGateway

    @Binds
    @Singleton
    abstract fun bindMealRepository(
        repository: RoomMealRepository,
    ): MealRepository

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
