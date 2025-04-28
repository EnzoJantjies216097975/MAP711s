package com.map711s.namibiahockey.data.model

data class EventEntry(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val location: String = "",
    val registrationDeadline: String = "",
    val isRegistered: Boolean = false,
    val registeredTeams: Int = 0
)  {
    // Extension function to convert EventEntry to HashMap
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "startDate" to startDate,
            "endDate" to endDate,
            "location" to location,
            "registrationDeadline" to registrationDeadline,
            "isRegistered" to isRegistered,
            "registeredTeams" to registeredTeams
        )
    }
}