package com.andrewenfusion.alphafitness.di

import android.content.Context
import androidx.room.Room
import com.andrewenfusion.alphafitness.core.database.AlphaFitnessDatabase
import com.andrewenfusion.alphafitness.core.database.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAlphaFitnessDatabase(
        @ApplicationContext context: Context,
    ): AlphaFitnessDatabase =
        Room.databaseBuilder(
            context,
            AlphaFitnessDatabase::class.java,
            AlphaFitnessDatabase.DATABASE_NAME,
        ).build()

    @Provides
    fun provideUserProfileDao(database: AlphaFitnessDatabase): UserProfileDao =
        database.userProfileDao()
}
