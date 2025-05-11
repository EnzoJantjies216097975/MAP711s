package com.map711s.namibiahockey.domain.usecase.event

import javax.inject.Inject

class UnregisterFromEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(eventId: String): Result<Unit> {
        if (eventId.isBlank()) {
            return Result.failure(IllegalArgumentException("Event ID cannot be empty"))
        }
        return eventRepository.unregisterFromEvent(eventId)
    }
}