package com.map711s.namibiahockey.domain.usecase.auth

import com.map711s.namibiahockey.domain.model.User
import com.map711s.namibiahockey.domain.repository.AuthRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: String = ""): Result<User> {
        return authRepository.getUserProfile(userId)
    }
}