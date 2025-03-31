package com.map711s.namibiahockey.data.repository

import com.map711s.namibiahockey.data.local.PreferencesManager
import com.map711s.namibiahockey.data.local.dao.UserDao
import com.map711s.namibiahockey.data.models.*
import com.map711s.namibiahockey.data.remote.AuthService
import com.map711s.namibiahockey.data.remote.UserService
import com.map711s.namibiahockey.util.NetworkBoundResource
import com.map711s.namibiahockey.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing user data and authentication
 */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val authService: AuthService,
    private val userService: UserService,
    private val preferencesManager: PreferencesManager
) {
    // Authentication
    suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val response = authService.login(LoginRequest(email, password))

            // Save auth token and user info
            preferencesManager.saveAuthToken(response.token)
            preferencesManager.saveUserInfo(
                response.user.id,
                response.user.name,
                response.user.email
            )

            // Save user to local database
            userDao.insertUser(response.user)

            Resource.Success(response.user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    suspend fun register(name: String, email: String, password: String, phone: String? = null): Resource<User> {
        return try {
            val response = authService.register(RegisterRequest(name, email, password, phone))

            // Save auth token and user info
            preferencesManager.saveAuthToken(response.token)
            preferencesManager.saveUserInfo(
                response.user.id,
                response.user.name,
                response.user.email
            )

            // Save user to local database
            userDao.insertUser(response.user)

            Resource.Success(response.user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    suspend fun logout() {
        try {
            // Attempt to notify the server about logout
            authService.logout()
        } catch (e: Exception) {
            // Continue with local logout even if server request fails
        } finally {
            // Clear local auth state
            preferencesManager.logout()
        }
    }

    suspend fun resetPassword(email: String): Resource<Unit> {
        return try {
            authService.resetPassword(PasswordResetRequest(email))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Password reset failed")
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Resource<Unit> {
        return try {
            authService.changePassword(PasswordChangeRequest(oldPassword, newPassword))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Password change failed")
        }
    }

    // User profile
    fun getCurrentUserProfile(): Flow<Resource<UserProfile>> {
        val userId = preferencesManager.userId.value ?: return flow {
            emit(Resource.Error("User not logged in"))
        }

        return NetworkBoundResource(
            query = {
                userDao.getUserProfileFlow(userId).first() ?: UserProfile(
                    id = userId,
                    name = preferencesManager.userName.value ?: "",
                    email = preferencesManager.userEmail.value ?: ""
                )
            },
            fetch = {
                userService.getUserProfile()
            },
            saveFetchResult = { profile ->
                userDao.updateUserProfile(profile)
            },
            shouldFetch = { profile ->
                profile.id.isEmpty()
            }
        ).asFlow()
    }

    suspend fun updateUserProfile(name: String, phone: String?, photoUrl: String?): Resource<User> {
        return try {
            val userId = preferencesManager.userId.value ?: return Resource.Error("User not logged in")
            val user = userDao.getUser(userId) ?: return Resource.Error("User not found")

            val updatedUser = user.copy(
                name = name,
                phone = phone,
                profilePhotoUrl = photoUrl
            )

            // Update on server
            val response = userService.updateProfile(updatedUser)

            // Update local database
            userDao.updateUser(response)

            // Update preferences
            preferencesManager.saveUserInfo(
                response.id,
                response.name,
                response.email
            )

            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Profile update failed")
        }
    }

    // User teams
    fun getUserTeams(): Flow<Resource<List<UserTeam>>> = flow {
        emit(Resource.Loading())

        try {
            val userId = preferencesManager.userId.value
            if (userId == null) {
                emit(Resource.Error("User not logged in"))
                return@flow
            }

            // First try to get from local database
            val localTeams = userDao.getUserTeams(userId)
            emit(Resource.Success(localTeams))

            // Then try to fetch from network
            try {
                val remoteTeams = userService.getUserTeams()
                // Update local database
                userDao.updateUserTeams(userId, remoteTeams)
                emit(Resource.Success(remoteTeams))
            } catch (e: Exception) {
                // Network fetch failed, but we already emitted local data
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to get user teams"))
        }
    }

    // Check if user is logged in
    fun isLoggedIn(): Flow<Boolean> = preferencesManager.isLoggedIn

    // Get current user ID
    fun getCurrentUserId(): String? = preferencesManager.userId.value
}