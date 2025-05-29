package com.map711s.namibiahockey.data.model

import java.util.Date

data class EventRegistration(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userRole: UserRole = UserRole.PLAYER,
    val teamId: String = "",
    val teamName: String = "",
    val registrationDate: Date = Date(),
    val status: RegistrationStatus = RegistrationStatus.CONFIRMED
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "eventId" to eventId,
            "userId" to userId,
            "userName" to userName,
            "userRole" to userRole.name,
            "teamId" to teamId,
            "teamName" to teamName,
            "registrationDate" to registrationDate,
            "status" to status.name
        )
    }
}

enum class RegistrationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    WAITLISTED
}