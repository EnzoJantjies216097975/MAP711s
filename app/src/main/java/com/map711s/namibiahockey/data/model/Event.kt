package com.map711s.namibiahockey.data.model

import java.util.Date

data class Event(
    val id: String = "",
    val name: String = "",
    val hockeyType: HockeyType= HockeyType.OUTDOOR,
    val description: String = "",
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val location: String = "",
    val eventType: EventType = EventType.TOURNAMENT,
    val registrationDeadline: Date = Date(),
    val registeredTeams: List<String> = emptyList(), // Team IDs
    val photoUrl: String = ""
)