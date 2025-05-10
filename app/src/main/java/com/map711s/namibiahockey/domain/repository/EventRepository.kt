package com.map711s.namibiahockey.domain.repository

interface EventRepository {
    suspend fun createEvent(event: Event): Result<String>
    suspend fun getEvent(eventId: String): Result<Event>
    suspend fun updateEvent(event: Event): Result<Unit>
    suspend fun deleteEvent(eventId: String): Result<Unit>
    suspend fun getAllEvents(): Result<List<Event>>
    suspend fun registerForEvent(eventId: String): Result<Unit>
    suspend fun unregisterFromEvent(eventId: String): Result<Unit>
}