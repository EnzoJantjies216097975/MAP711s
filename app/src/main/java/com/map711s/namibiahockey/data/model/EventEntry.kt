package com.map711s.namibiahockey.data.model

data class EventEntry(
    val id: String,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val location: String,
    val registrationDeadline: String,
    val isRegistered: Boolean,
    val registeredTeams: Int
)