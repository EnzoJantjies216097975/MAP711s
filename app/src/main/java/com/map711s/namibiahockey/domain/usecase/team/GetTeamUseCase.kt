package com.map711s.namibiahockey.domain.usecase.team

import com.map711s.namibiahockey.data.model.Team
import javax.inject.Inject

class GetTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(teamId: String): Result<Team> {
        if (teamId.isBlank()) {
            return Result.failure(IllegalArgumentException("Team ID cannot be empty"))
        }
        return teamRepository.getTeam(teamId)
    }
}