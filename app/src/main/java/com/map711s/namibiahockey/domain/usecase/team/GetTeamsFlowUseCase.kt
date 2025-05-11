package com.map711s.namibiahockey.domain.usecase.team

import com.map711s.namibiahockey.data.model.Team
import javax.inject.Inject

class GetTeamsFlowUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    operator fun invoke(): Flow<List<Team>> {
        return teamRepository.getTeamsFlow()
    }
}