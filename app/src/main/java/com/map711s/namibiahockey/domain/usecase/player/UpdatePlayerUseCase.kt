package com.map711s.namibiahockey.domain.usecase.player

import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.domain.repository.PlayerRepository
import javax.inject.Inject

class UpdatePlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(player: Player): Result<Unit> {
        // Validate player data
        if (player.id.isBlank()) {
            return Result.failure(IllegalArgumentException("Player ID is required for updates"))
        }

        if (player.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Required player fields are missing"))
        }

        return playerRepository.updatePlayer(player)
    }
}