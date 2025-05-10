package com.map711s.namibiahockey.domain.model

import java.util.Date

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val startDate: Date,
    val endDate: Date,
    val location: String,
    val registrationDeadline: Date,
    val isRegistered: Boolean,
    val registeredTeams: Int
)
