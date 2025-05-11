package com.map711s.namibiahockey.domain.repository

import com.map711s.namibiahockey.data.model.EventEntry

interface EventRepository {
    suspend fun createEvent(event: EventEntry): Result<String>
    suspend fun getEvent(eventId: String): Result<EventEntry>
    suspend fun updateEvent(event: EventEntry): Result<Unit>
    suspend fun deleteEvent(eventId: String): Result<Unit>
    suspend fun getAllEvents(): Result<List<EventEntry>>
    suspend fun registerForEvent(eventId: String): Result<Unit>
    suspend fun unregisterFromEvent(eventId: String): Result<Unit>
}