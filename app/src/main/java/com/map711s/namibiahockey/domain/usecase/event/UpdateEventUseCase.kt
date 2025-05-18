package com.map711s.namibiahockey.domain.usecase.event

import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.domain.repository.EventRepository

class UpdateEventUseCase(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(event: EventEntry): Result<Unit> {
        // Validate event data
        if (event.id.isBlank()) {
            return Result.failure(IllegalArgumentException("Event ID is required for updates"))
        }

        if (event.title.isBlank() || event.description.isBlank()) {
            return Result.failure(IllegalArgumentException("Required event fields are missing"))
        }

        return eventRepository.updateEvent(event)
    }
}