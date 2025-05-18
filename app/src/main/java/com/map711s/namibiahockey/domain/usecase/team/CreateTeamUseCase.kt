package com.map711s.namibiahockey.domain.usecase.team

import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.domain.repository.TeamRepository

class CreateTeamUseCase(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(team: Team): Result<String> {
        // Validate team data
        if (team.name.isBlank() || team.category.isBlank() || team.division.isBlank()) {
            return Result.failure(IllegalArgumentException("Required team fields are missing"))
        }

        return teamRepository.createTeam(team)
    }
}