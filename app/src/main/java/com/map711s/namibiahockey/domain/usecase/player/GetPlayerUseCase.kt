package com.map711s.namibiahockey.domain.usecase.player

import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.domain.repository.PlayerRepository
import javax.inject.Inject

class GetPlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(playerId: String): Result<Player> {
        if (playerId.isBlank()) {
            return Result.failure(IllegalArgumentException("Player ID cannot be empty"))
        }
        return playerRepository.getPlayer(playerId)
    }
}