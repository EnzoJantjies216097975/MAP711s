package com.map711s.namibiahockey.domain.usecase.team

import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.domain.repository.TeamRepository

class GetAllTeamsUseCase(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(): Result<List<Team>> {
        return teamRepository.getAllTeams()
    }
}