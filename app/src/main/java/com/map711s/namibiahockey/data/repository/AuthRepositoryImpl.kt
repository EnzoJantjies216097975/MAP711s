package com.map711s.namibiahockey.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.mapper.toData
import com.map711s.namibiahockey.data.mapper.toDomain
import com.map711s.namibiahockey.data.remote.firebase.FirebaseUserDataSource
import com.map711s.namibiahockey.domain.model.User
import com.map711s.namibiahockey.domain.model.UserRole
import com.map711s.namibiahockey.domain.repository.AuthRepository
import com.map711s.namibiahockey.util.SecureStorageManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.map711s.namibiahockey.data.remote.model.FirebaseUser as CustomFirebaseUser

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDataSource: FirebaseUserDataSource,
    private val secureStorageManager: SecureStorageManager,
    @ApplicationContext private val context: Context
) : AuthRepository {

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid ?: secureStorageManager.getUserId()
    }

    override suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Login failed: User is null"))

            // Store user ID in secure storage
            secureStorageManager.storeUserId(firebaseUser.uid)

            // Store refresh token if available
            firebaseUser.getIdToken(true).await()?.token?.let { token ->
                secureStorageManager.storeAuthToken(token)
            }

            Result.success(firebaseUser.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logoutUser(): Result<Unit> {
        return try {
            auth.signOut()

            // Clear secure storage
            secureStorageManager.clearAuthToken()
            secureStorageManager.clearUserId()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

            // Save user to firestore (convert to your custom FirebaseUser)
            val customFirebaseUser = CustomFirebaseUser(
                id = firebaseUser.uid,
                email = email,
                name = name,
                phone = phone,
                role = role.name
            )
            userDataSource.saveUser(customFirebaseUser)

            // Save user to Firestore as a generic map
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(mapOf(
                    "id" to user.id,
                    "email" to user.email,
                    "name" to user.name,
                    "phone" to user.phone,
                    "role" to user.role.name
                ))
                .await()

            Result.success(firebaseUser.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            val actualUserId = userId.ifBlank { getCurrentUserId() ?: "" }
            val customUser = userDataSource.getUser(actualUserId)
                ?: return Result.failure(Exception("User not found"))

            // Convert from data model to domain model
            val domainUser = User(
                id = customUser.id,
                email = customUser.email,
                name = customUser.name,
                phone = customUser.phone,
                role = try {
                    UserRole.valueOf(customUser.role)
                } catch (e: IllegalArgumentException) {
                    UserRole.PLAYER // Default
                }
            )

            Result.success(domainUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            // Convert domain model to data model
            val customFirebaseUser = CustomFirebaseUser(
                id = user.id,
                email = user.email,
                name = user.name,
                phone = user.phone,
                role = user.role.name
            )
            userDataSource.updateUser(customFirebaseUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}