package com.map711s.namibiahockey.domain.usecase.player

import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.domain.repository.PlayerRepository

class GetPlayersByTeamUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(teamId: String): Result<List<Player>> {
        if (teamId.isBlank()) {
            return Result.failure(IllegalArgumentException("Team ID cannot be empty"))
        }
        return playerRepository.getPlayersByTeam(teamId)
    }
}