package com.map711s.namibiahockey.domain.usecase.player

import com.map711s.namibiahockey.domain.repository.PlayerRepository
import javax.inject.Inject

class DeletePlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(playerId: String): Result<Unit> {
        if (playerId.isBlank()) {
            return Result.failure(IllegalArgumentException("Player ID cannot be empty"))
        }
        return playerRepository.deletePlayer(playerId)
    }
}