package com.map711s.namibiahockey.domain.usecase.auth

import com.map711s.namibiahockey.data.mapper.toDomain  // Import the mapper
import com.map711s.namibiahockey.domain.model.User
import com.map711s.namibiahockey.domain.repository.AuthRepository


class GetUserProfileUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: String = ""): Result<User> {
        return authRepository.getUserProfile(userId)
//        return when {
//            result.isSuccess -> Result.success(
//                result.getOrNull()?.toDomain() ?: error("User is null")
//            )
//
//            else -> Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
//        }
    }
}