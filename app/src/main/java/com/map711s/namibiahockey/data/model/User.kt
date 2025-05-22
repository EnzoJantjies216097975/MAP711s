package com.map711s.namibiahockey.data.model

// User model for authentication and user profiles
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.PLAYER,
    val profileImageUrl: String = "",
    val dateOfBirth: String = "", // Format: yyyy-MM-dd
    val nationality: String = "Namibian",
    val emergencyContact: String = "",
    val emergencyPhone: String = "",
    val address: String = "",
    val bio: String = "",
    val joinedDate: Date = Date(),
    val isActive: Boolean = true,
    // Player-specific fields (only used if role is PLAYER)
    val playerStats: PlayerStats? = null,
    val currentTeamId: String = "",
    val position: String = "",
    val jerseyNumber: Int = 0,
    val hockeyType: HockeyType = HockeyType.BOTH
) {
    fun toHashMap(): HashMap<String, Any> {
        val map = hashMapOf(
            "id" to id,
            "email" to email,
            "name" to name,
            "phone" to phone,
            "role" to role.name,
            "profileImageUrl" to profileImageUrl,
            "dateOfBirth" to dateOfBirth,
            "nationality" to nationality,
            "emergencyContact" to emergencyContact,
            "emergencyPhone" to emergencyPhone,
            "address" to address,
            "bio" to bio,
            "joinedDate" to joinedDate,
            "isActive" to isActive,
            "currentTeamId" to currentTeamId,
            "position" to position,
            "jerseyNumber" to jerseyNumber,
            "hockeyType" to hockeyType.name
        )

        // Add player stats if available
        playerStats?.let { stats ->
            map["playerStats"] = hashMapOf(
                "goalsScored" to stats.goalsScored,
                "assists" to stats.assists,
                "gamesPlayed" to stats.gamesPlayed,
                "yellowCards" to stats.yellowCards,
                "redCards" to stats.redCards
            )
        }

        return map
    }
}