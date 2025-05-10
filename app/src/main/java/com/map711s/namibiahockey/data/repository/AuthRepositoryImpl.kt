package com.map711s.namibiahockey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.mapper.toData
import com.map711s.namibiahockey.data.mapper.toDomain
import com.map711s.namibiahockey.data.remote.firebase.FirebaseUserDataSource
import com.map711s.namibiahockey.domain.model.User
import com.map711s.namibiahockey.domain.model.UserRole
import com.map711s.namibiahockey.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDataSource: FirebaseUserDataSource
    //private val context: Context
) : AuthRepository {

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid

    }

    override suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        phone: String,
        role: UserRole
    ): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Registration failed: User is null"))

            // Create user profile in Firestore
            val user = User(
                id = firebaseUser.uid,
                email = email,
                name = name,
                phone = phone,
                role = role
            )

            // Save user to firestore
            userDataSource.saveUser(user.toData())

            Result.success(firebaseUser.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}