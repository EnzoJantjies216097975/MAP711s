package com.map711s.namibiahockey.domain.usecase.team

import com.map711s.namibiahockey.domain.repository.TeamRepository
import javax.inject.Inject

class RemovePlayerFromTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(teamId: String, playerId: String): Result<Unit> {
        if (teamId.isBlank() || playerId.isBlank()) {
            return Result.failure(IllegalArgumentException("Team ID and Player ID cannot be empty"))
        }
        return teamRepository.removePlayerFromTeam(teamId, playerId)
    }
}