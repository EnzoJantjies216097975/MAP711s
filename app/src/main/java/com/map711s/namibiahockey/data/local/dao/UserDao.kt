package com.map711s.namibiahockey.data.local.dao

import androidx.room.*
import com.map711s.namibiahockey.data.models.User
import com.map711s.namibiahockey.data.models.UserProfile
import com.map711s.namibiahockey.data.models.UserTeam
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User-related operations in the database.
 * This interface defines methods to interact with user data stored locally.
 */
@Dao
interface UserDao {

    /**
     * Insert a user into the database, replacing any existing user with the same ID.
     * @param user The user entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    /**
     * Update an existing user in the database.
     * @param user The user entity with updated data.
     */
    @Update
    suspend fun updateUser(user: User)

    /**
     * Delete a user from the database.
     * @param user The user entity to delete.
     */
    @Delete
    suspend fun deleteUser(user: User)

    /**
     * Get a user by their ID.
     * @param userId The unique identifier of the user.
     * @return The user entity or null if not found.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUser(userId: String): User?

    /**
     * Get a user by their email address.
     * @param email The email address of the user.
     * @return The user entity or null if not found.
     */
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    /**
     * Get a user by their ID, returning as a Flow to observe changes.
     * @param userId The unique identifier of the user.
     * @return A Flow emitting the user entity whenever it changes.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserFlow(userId: String): Flow<User?>

    /**
     * Get a user profile by their ID, returning as a Flow to observe changes.
     * This would typically join with other tables to get team information.
     * @param userId The unique identifier of the user.
     * @return A Flow emitting the user profile whenever it changes.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserProfileFlow(userId: String): Flow<UserProfile?>

    /**
     * Update a user profile with associated data.
     * In a real implementation, this would handle the user's teams and other related data.
     * @param profile The user profile to update.
     */
    @Transaction
    suspend fun updateUserProfile(profile: UserProfile) {
        // Update the basic user information
        profile.let {
            val user = User(
                id = it.id,
                email = it.email,
                name = it.name,
                phone = it.phone,
                profilePhotoUrl = it.profilePhotoUrl,
                role = it.role,
                isEmailVerified = true, // Assuming verified if we're updating profile
                isActive = true
            )
            insertUser(user)
        }

        // In a real implementation, we would also update the user's teams
        // This might involve deleting old team associations and creating new ones
    }

    /**
     * Get all teams associated with a user.
     * In a real implementation, this would join with the teams table.
     * @param userId The unique identifier of the user.
     * @return A list of teams the user is associated with.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserTeams(userId: String): List<UserTeam>

    /**
     * Update the teams associated with a user.
     * This would typically involve deleting old associations and creating new ones.
     * @param userId The unique identifier of the user.
     * @param teams The list of teams to associate with the user.
     */
    @Transaction
    suspend fun updateUserTeams(userId: String, teams: List<UserTeam>) {
        // In a real implementation, we would:
        // 1. Delete old team associations for this user
        // 2. Create new team associations based on the provided list
    }
}