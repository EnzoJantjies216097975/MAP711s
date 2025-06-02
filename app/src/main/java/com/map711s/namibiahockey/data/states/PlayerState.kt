package com.map711s.namibiahockey.data.states

import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.data.model.PlayerProfile

// State for a single Player
data class PlayerState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val player: Player? = null,
    val playerId: String? = null,
    val error: String? = null
)

// State for a list of Players - FIXED: Added playerListItems
data class PlayerListState(
    val isLoading: Boolean = false,
    val players: List<Player> = emptyList(),
    val playerListItems: List<Player> = emptyList(),
    val error: String? = null
)

// State for player profile details
data class PlayerProfileState(
    val isLoading: Boolean = false,
    val playerProfile: PlayerProfile? = null,
    val error: String? = null
)