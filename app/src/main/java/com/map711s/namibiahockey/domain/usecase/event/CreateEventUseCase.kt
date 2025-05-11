package com.map711s.namibiahockey.domain.usecase.event


import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.domain.repository.EventRepository
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(event: EventEntry): Result<String> {
        return eventRepository.createEvent(event)
    }
}