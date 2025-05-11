package com.map711s.namibiahockey.domain.usecase.team

import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.domain.repository.TeamRepository
import javax.inject.Inject

class UpdateTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(team: Team): Result<Unit> {
        // Validate team data
        if (team.id.isBlank()) {
            return Result.failure(IllegalArgumentException("Team ID is required for updates"))
        }

        if (team.name.isBlank() || team.category.isBlank()) {
            return Result.failure(IllegalArgumentException("Required team fields are missing"))
        }

        return teamRepository.updateTeam(team)
    }
}