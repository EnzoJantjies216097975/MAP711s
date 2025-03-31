package com.map711s.namibiahockey.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Represents a user in the application
 */
@Entity(tableName = "users")
@Serializable
data class User(
    @PrimaryKey
    val id: String,
    val email: String,
    val name: String,
    val phone: String? = null,
    val profilePhotoUrl: String? = null,
    val role: UserRole = UserRole.PLAYER,
    val dateJoined: Long = System.currentTimeMillis(),
    val isEmailVerified: Boolean = false,
    val isActive: Boolean = true
)

/**
 * Role of a user in the application
 */
enum class UserRole {
    ADMIN,      // Hockey union administrator
    COACH,      // Team coach
    MANAGER,    // Team manager
    PLAYER,     // Player
    REFEREE,    // Match referee
    SPECTATOR   // General user/spectator
}

/**
 * User profile info displayed in the UI
 */
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val profilePhotoUrl: String? = null,
    val role: UserRole = UserRole.PLAYER,
    val teams: List<UserTeam> = emptyList(),
    val isAdmin: Boolean = false
)

/**
 * Simplified team info for user profile
 */
data class UserTeam(
    val id: String,
    val name: String,
    val division: String,
    val role: TeamRole
)

/**
 * Role of a user in a team
 */
enum class TeamRole {
    COACH,
    CAPTAIN,
    PLAYER,
    MANAGER
}

/**
 * Authentication response from API
 */
@Serializable
data class AuthResponse(
    val token: String,
    val user: User
)

/**
 * Login request payload
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Registration request payload
 */
@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null
)

/**
 * Password change request
 */
@Serializable
data class PasswordChangeRequest(
    val oldPassword: String,
    val newPassword: String
)

/**
 * Password reset request
 */
@Serializable
data class PasswordResetRequest(
    val email: String
)