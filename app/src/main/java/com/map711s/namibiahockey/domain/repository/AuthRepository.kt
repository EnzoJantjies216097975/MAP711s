package com.map711s.namibiahockey.domain.repository

import com.map711s.namibiahockey.data.model.User
import com.map711s.namibiahockey.data.model.UserRole

interface AuthRepository {
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserId(): String?
    suspend fun registerUser(email: String, password: String, name: String, phone: String, role: UserRole): Result<String>
    suspend fun loginUser(email: String, password: String): Result<String>
    suspend fun logoutUser(): Result<Unit>
    suspend fun getUserProfile(userId: String = ""): Result<User>
    suspend fun updateUserProfile(user: com.map711s.namibiahockey.domain.model.User): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
}