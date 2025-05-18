package com.map711s.namibiahockey.domain.usecase.team

import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow

class GetTeamsFlowUseCase(
    private val teamRepository: TeamRepository
) {
    operator fun invoke(): Flow<List<Team>> {
        return teamRepository.getTeamsFlow()
    }
}