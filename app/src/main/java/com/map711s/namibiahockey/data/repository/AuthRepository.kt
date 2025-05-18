package com.map711s.namibiahockey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.User
import com.map711s.namibiahockey.data.model.UserRole
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    // Check if user is currently logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Get current user ID
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Register a new user
    suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        phone: String,
        role: UserRole = UserRole.PLAYER
    ): Result<FirebaseUser>{
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Registration failed: User is null"))

            // Create user profile in Firestore
            val user = com.map711s.namibiahockey.data.model.User(
                id = firebaseUser.uid,
                email = email,
                name = name,
                phone = phone,
                role = role
            )

            // Save user to Firestore
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login user with email and password
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Login failed: User is null"))

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Logout user
    suspend fun logoutUser(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Get user profile data
    suspend fun getUserProfile(userId: String = getCurrentUserId() ?: ""): Result<User> {
        return try {
            if (userId.isEmpty()) {
                return Result.failure(Exception("User ID is empty"))
            }

            val documentSnapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                    ?: return Result.failure(Exception("Failed to parse user data"))
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update user profile
    suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(user.id)
                .set(user)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reset password
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}