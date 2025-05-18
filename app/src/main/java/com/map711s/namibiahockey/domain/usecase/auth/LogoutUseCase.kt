package com.map711s.namibiahockey.domain.usecase.auth

import com.map711s.namibiahockey.domain.repository.AuthRepository

class LogoutUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logoutUser()
    }
}