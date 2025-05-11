package com.map711s.namibiahockey.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.util.Date

// Player model for player management
data class Player(
    @DocumentId val id: String = "",
    val userId: String = "", // Reference to User
    val name: String = "",
    val dateOfBirth: Date = Date(),
    val position: String = "",
    val jerseyNumber: Int = 0,
    val teamId: String = "",
    val contactNumber: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val stats: PlayerStats = PlayerStats(),
    val isActive: Boolean = true,
    val nationality: String = "Namibian",
    val height: Int = 0, // in centimeters
    val weight: Int = 0, // in kilograms
    val dominantSide: String = "", // left or right
    val joinedDate: Date = Date()
) {
    @Exclude
    fun toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "userId" to userId,
            "name" to name,
            "dateOfBirth" to dateOfBirth,
            "position" to position,
            "jerseyNumber" to jerseyNumber,
            "teamId" to teamId,
            "contactNumber" to contactNumber,
            "email" to email,
            "photoUrl" to photoUrl,
            "stats" to stats,
            "isActive" to isActive,
            "nationality" to nationality,
            "height" to height,
            "weight" to weight,
            "dominantSide" to dominantSide,
            "joinedDate" to joinedDate
        )
    }
}