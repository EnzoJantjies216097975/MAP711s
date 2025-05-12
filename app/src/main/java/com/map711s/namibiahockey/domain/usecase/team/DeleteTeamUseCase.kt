package com.map711s.namibiahockey.domain.usecase.team

import com.map711s.namibiahockey.domain.repository.TeamRepository
import javax.inject.Inject

class DeleteTeamUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(teamId: String): Result<Unit> {
        if (teamId.isBlank()) {
            return Result.failure(IllegalArgumentException("Team ID cannot be empty"))
        }
        return teamRepository.deleteTeam(teamId)
    }
}