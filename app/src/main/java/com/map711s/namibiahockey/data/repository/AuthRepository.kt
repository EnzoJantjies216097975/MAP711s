package com.map711s.namibiahockey.data.repository

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
    companion object {
        private const val TAG = "AuthRepository"
    }

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
            Log.d(TAG, "Attempting to register user: $email")

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Registration failed: User is null"))

            Log.d(TAG, "User registered successfully: ${firebaseUser.uid}")

            // Create user profile in Firestore
            val user = User(
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

            Log.d(TAG, "User profile saved to Firestore")
            Result.success(firebaseUser)

        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            val errorMessage = getAuthErrorMessage(e)
            Result.failure(Exception(errorMessage))
        }
    }

    // Login user with email and password
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "Attempting to login user: $email")

            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Login failed: User is null"))

            Log.d(TAG, "User logged in successfully: ${firebaseUser.uid}")
            Result.success(firebaseUser)

        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            val errorMessage = getAuthErrorMessage(e)
            Result.failure(Exception(errorMessage))
        }
    }

    //Logout user
    suspend fun logoutUser(): Result<Unit> {
        return try {
            Log.d(TAG, "Attempting to logout user")
            auth.signOut()
            Log.d(TAG, "User logged out successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Logout failed", e)
            Result.failure(e)
        }
    }

    //Get user profile data
    suspend fun getUserProfile(userId: String = getCurrentUserId() ?: ""): Result<User> {
        return try {
            if (userId.isEmpty()) {
                return Result.failure(Exception("User ID is empty"))
            }

            Log.d(TAG, "Fetching user profile: $userId")

            val documentSnapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                    ?: return Result.failure(Exception("Failed to parse user data"))
                Log.d(TAG, "User profile fetched successfully")
                Result.success(user)
            } else {
                Log.w(TAG, "User profile not found: $userId")
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch user profile", e)
            Result.failure(e)
        }
    }

    // Update user profile
    suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            Log.d(TAG, "Updating user profile: ${user.id}")

            firestore.collection("users")
                .document(user.id)
                .set(user)
                .await()

            Log.d(TAG, "User profile updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update user profile", e)
            Result.failure(e)
        }
    }

    // Reset password
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            Log.d(TAG, "Sending password reset email to: $email")

            auth.sendPasswordResetEmail(email).await()

            Log.d(TAG, "Password reset email sent successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send password reset email", e)
            val errorMessage = getAuthErrorMessage(e)
            Result.failure(Exception(errorMessage))
        }
    }

    private fun getAuthErrorMessage(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                "Invalid email or password. Please check your credentials."
            }
            is FirebaseAuthInvalidUserException -> {
                "No account found with this email address."
            }
            is FirebaseAuthUserCollisionException -> {
                "An account with this email already exists."
            }
            is FirebaseTooManyRequestsException -> {
                "Too many failed attempts. Please try again later."
            }
            is FirebaseNetworkException -> {
                "Network error. Please check your internet connection and try again."
            }
            else -> {
                when {
                    exception.message?.contains("network error", ignoreCase = true) == true -> {
                        "Network connection problem. Please check your internet and try again."
                    }
                    exception.message?.contains("timeout", ignoreCase = true) == true -> {
                        "Request timed out. Please try again."
                    }
                    exception.message?.contains("host", ignoreCase = true) == true -> {
                        "Cannot connect to authentication service. Please try again later."
                    }
                    else -> {
                        exception.message ?: "Authentication failed. Please try again."
                    }
                }
            }
        }
    }
}