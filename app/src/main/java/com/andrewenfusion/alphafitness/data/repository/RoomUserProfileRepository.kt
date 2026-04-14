package com.andrewenfusion.alphafitness.data.repository

import com.andrewenfusion.alphafitness.core.common.dispatcher.AppDispatchers
import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.core.database.dao.UserProfileDao
import com.andrewenfusion.alphafitness.data.mapper.toDomain
import com.andrewenfusion.alphafitness.data.mapper.toEntity
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomUserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val dispatchers: AppDispatchers,
) : UserProfileRepository {
    override suspend fun getUserProfile(userId: String): UserProfile? =
        withContext(dispatchers.io) {
            userProfileDao.getById(userId)?.toDomain()
        }

    override fun observeUserProfile(userId: String): Flow<UserProfile?> =
        userProfileDao
            .observeById(userId)
            .map { entity -> entity?.toDomain() }
            .flowOn(dispatchers.io)

    override suspend fun upsertUserProfile(profile: UserProfile): AppResult<Unit> =
        withContext(dispatchers.io) {
            runCatching {
                userProfileDao.upsert(profile.toEntity())
            }.fold(
                onSuccess = { AppResult.Success(Unit) },
                onFailure = { throwable ->
                    AppResult.Failure(
                        AppError.Storage(
                            message = throwable.message ?: "Failed to store user profile.",
                        ),
                    )
                },
            )
        }
}
