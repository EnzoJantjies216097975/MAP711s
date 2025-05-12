package com.map711s.namibiahockey.domain.repository

import com.map711s.namibiahockey.domain.model.User

interface AuthRepository {
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserId(): String?
    fun registerUser(email: String,
                     password: String,
                     name: String,
                     phone: String,
                     role: com.map711s.namibiahockey.domain.model.UserRole
    ): Result<String>
    suspend fun loginUser(email: String, password: String): Result<String>
    suspend fun logoutUser(): Result<Unit>
    suspend fun getUserProfile(userId: String): Result<User>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
}