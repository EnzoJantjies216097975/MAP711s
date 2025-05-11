package com.map711s.namibiahockey.domain.usecase.auth

import com.map711s.namibiahockey.domain.model.User
import com.map711s.namibiahockey.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        // Validate user data
        if (user.email.isBlank() || user.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and name are required"))
        }

        return authRepository.updateUserProfile(user)
    }
}