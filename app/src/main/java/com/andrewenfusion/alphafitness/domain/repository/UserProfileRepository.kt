package com.andrewenfusion.alphafitness.domain.repository

import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeUserProfile(userId: String = UserProfile.LOCAL_USER_ID): Flow<UserProfile?>

    suspend fun upsertUserProfile(profile: UserProfile): AppResult<Unit>
}
