package com.map711s.namibiahockey.domain.usecase.event

import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.domain.repository.EventRepository

class GetAllEventsUseCase(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(): Result<List<EventEntry>> {
        return eventRepository.getAllEvents()
    }
}