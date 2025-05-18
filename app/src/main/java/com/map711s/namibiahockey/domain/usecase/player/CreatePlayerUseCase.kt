package com.map711s.namibiahockey.domain.usecase.player

import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.domain.repository.PlayerRepository

class CreatePlayerUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(player: Player): Result<String> {
        // Validate player data
        if (player.name.isBlank() || player.position.isBlank()) {
            return Result.failure(IllegalArgumentException("Required player fields are missing"))
        }

        return playerRepository.createPlayer(player)
    }
}