package com.map711s.namibiahockey.domain.usecase.auth

import com.map711s.namibiahockey.domain.repository.AuthRepository

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }
        return authRepository.resetPassword(email)
    }
}