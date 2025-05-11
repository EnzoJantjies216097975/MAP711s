package com.map711s.namibiahockey.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.User
import com.map711s.namibiahockey.data.model.UserRole
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserId(): String?
    suspend fun registerUser(email: String, password: String, name: String, phone: String, role: UserRole): Result<String>
    suspend fun loginUser(email: String, password: String): Result<String>
    suspend fun logoutUser(): Result<Unit>
    suspend fun getUserProfile(userId: String = ""): Result<User>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
}