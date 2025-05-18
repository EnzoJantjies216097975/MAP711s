package com.map711s.namibiahockey.domain.usecase.auth

import com.map711s.namibiahockey.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password cannot be empty"))
        }
        return authRepository.loginUser(email, password)
    }
}