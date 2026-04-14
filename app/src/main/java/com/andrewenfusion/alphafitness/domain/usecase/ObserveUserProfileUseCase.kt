package com.andrewenfusion.alphafitness.domain.usecase

import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.domain.repository.UserProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository,
) {
    operator fun invoke(userId: String = UserProfile.LOCAL_USER_ID): Flow<UserProfile?> =
        repository.observeUserProfile(userId)
}
