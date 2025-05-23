package com.map711s.namibiahockey.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.data.model.PlayerListItem
import com.map711s.namibiahockey.data.model.PlayerProfile
import com.map711s.namibiahockey.data.model.PlayerProfileStats
import com.map711s.namibiahockey.data.model.PlayerStats
import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

// State for a single Player
data class PlayerState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val player: Player? = null,
    val playerId: String? = null,
    val error: String? = null
)

// State for a list of Players
data class PlayerListState(
    val isLoading: Boolean = false,
    val players: List<Player> = emptyList(),
    val playerListItems: List<PlayerListItem> = emptyList(),
    val error: String? = null
)

// State for player profile details
data class PlayerProfileState(
    val isLoading: Boolean = false,
    val playerProfile: PlayerProfile? = null,
    val error: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val TAG = "PlayerViewModel"

    // Player creation/update state
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    // Player list state
    private val _playerListState = MutableStateFlow(PlayerListState())
    val playerListState: StateFlow<PlayerListState> = _playerListState.asStateFlow()

    // Player profile state
    private val _playerProfileState = MutableStateFlow(PlayerProfileState())
    val playerProfileState: StateFlow<PlayerProfileState> = _playerProfileState.asStateFlow()

    // Local cache of teams for reference
    private val _teamsCache = mutableMapOf<String, Team>()

    /**
     * Create a new player in Firebase
     */
    fun createPlayer(player: Player) {
        _playerState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val playerMap = playerToMap(player)

                // If no id is provided, let Firestore generate one
                val documentReference = if (player.id.isBlank()) {
                    firestore.collection("players").add(playerMap).await()
                } else {
                    firestore.collection("players").document(player.id).set(playerMap).await()
                    firestore.collection("players").document(player.id)
                }

                val newPlayerId = player.id.ifBlank { documentReference.id }

                // If this player is added to a team, update the team's players list
                if (player.teamId.isNotBlank()) {
                    updateTeamPlayers(player.teamId, newPlayerId, true)
                }

                _playerState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        player = player.copy(id = newPlayerId),
                        playerId = newPlayerId
                    )
                }

                // Refresh the player list
                loadAllPlayers()

                Log.d(TAG, "Player created: $newPlayerId")

            } catch (e: Exception) {
                Log.e(TAG, "Error creating player", e)
                _playerState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to create player: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Get a player by ID
     */
    fun getPlayer(playerId: String) {
        _playerState.update { it.copy(isLoading = true, error = null, playerId = playerId) }

        viewModelScope.launch {
            try {
                val documentSnapshot = firestore.collection("players").document(playerId).get().await()

                if (documentSnapshot.exists()) {
                    val player = documentSnapshot.toObject(Player::class.java)
                        ?: throw Exception("Failed to parse player data")

                    _playerState.update {
                        it.copy(
                            isLoading = false,
                            player = player.copy(id = documentSnapshot.id)
                        )
                    }

                    Log.d(TAG, "Player retrieved: $playerId")

                } else {
                    throw Exception("Player not found")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error getting player", e)
                _playerState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to get player: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Get player profile details including team info
     */
    fun getPlayerProfile(playerId: String) {
        _playerProfileState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val documentSnapshot = firestore.collection("players").document(playerId).get().await()

                if (documentSnapshot.exists()) {
                    val player = documentSnapshot.toObject(Player::class.java)
                        ?: throw Exception("Failed to parse player data")

                    // Get team details if this player is on a team
                    var teamName = ""
                    var teamId = player.teamId

                    if (player.teamId.isNotBlank()) {
                        // Check cache first
                        val cachedTeam = _teamsCache[player.teamId]

                        if (cachedTeam != null) {
                            teamName = cachedTeam.name
                        } else {
                            try {
                                val teamDoc = firestore.collection("teams").document(player.teamId).get().await()
                                if (teamDoc.exists()) {
                                    val team = teamDoc.toObject(Team::class.java)
                                    teamName = team?.name ?: "Unknown Team"

                                    // Cache the team
                                    if (team != null) {
                                        _teamsCache[player.teamId] = team
                                    }
                                }
                            } catch (e: Exception) {
                                Log.w(TAG, "Error fetching team details", e)
                                teamName = "Unknown Team"
                            }
                        }
                    }

                    // Calculate age from date of birth
                    val age = if (player.dateOfBirth != Date(0)) {
                        val today = Date()
                        val diff = today.time - player.dateOfBirth.time
                        (diff / (1000L * 60 * 60 * 24 * 365.25)).toInt()
                    } else {
                        0
                    }

                    // Create player profile from player data
                    val playerProfile = PlayerProfile(
                        id = playerId,
                        name = player.name,
                        position = player.position,
                        jerseyNumber = player.jerseyNumber,
                        teamName = teamName,
                        teamId = teamId,
                        hockeyType = player.hockeyType,
                        age = age,
                        nationality = "Namibian", // Default for now
                        stats = PlayerProfileStats(
                            gamesPlayed = player.stats.gamesPlayed,
                            goalsScored = player.stats.goalsScored,
                            assists = player.stats.assists,
                            totalPoints = player.stats.goalsScored + player.stats.assists,
                            yellowCards = player.stats.yellowCards,
                            redCards = player.stats.redCards,
                            averageRating = 4.5, // Placeholder
                            seasonsPlayed = 1 // Placeholder
                        ),
                        isNationalPlayer = false, // Placeholder
                        photoUrl = player.photoUrl
                    )

                    _playerProfileState.update {
                        it.copy(
                            isLoading = false,
                            playerProfile = playerProfile
                        )
                    }

                    Log.d(TAG, "Player profile retrieved: $playerId")

                } else {
                    throw Exception("Player not found")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error getting player profile", e)
                _playerProfileState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to get player profile: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Update an existing player
     */
    fun updatePlayer(player: Player) {
        _playerState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Get the current player to check if team changed
                val oldPlayerDoc = firestore.collection("players").document(player.id).get().await()
                val oldPlayer = oldPlayerDoc.toObject(Player::class.java)

                // Update the player
                val playerMap = playerToMap(player)
                firestore.collection("players").document(player.id).set(playerMap).await()

                // Handle team changes if necessary
                if (oldPlayer != null && oldPlayer.teamId != player.teamId) {
                    // Remove from old team
                    if (oldPlayer.teamId.isNotBlank()) {
                        updateTeamPlayers(oldPlayer.teamId, player.id, false)
                    }

                    // Add to new team
                    if (player.teamId.isNotBlank()) {
                        updateTeamPlayers(player.teamId, player.id, true)
                    }
                }

                _playerState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        player = player
                    )
                }

                // Refresh the player list
                loadAllPlayers()

                Log.d(TAG, "Player updated: ${player.id}")

            } catch (e: Exception) {
                Log.e(TAG, "Error updating player", e)
                _playerState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to update player: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Delete a player
     */
    fun deletePlayer(playerId: String) {
        _playerState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Get the player first to check for team membership
                val playerDoc = firestore.collection("players").document(playerId).get().await()
                val player = playerDoc.toObject(Player::class.java)

                // Remove player from team if needed
                if (player != null && player.teamId.isNotBlank()) {
                    updateTeamPlayers(player.teamId, playerId, false)
                }

                // Delete the player
                firestore.collection("players").document(playerId).delete().await()

                _playerState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        player = null,
                        playerId = null
                    )
                }

                // Refresh the player list
                loadAllPlayers()

                Log.d(TAG, "Player deleted: $playerId")

            } catch (e: Exception) {
                Log.e(TAG, "Error deleting player", e)
                _playerState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to delete player: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Load all players
     */
    fun loadAllPlayers() {
        _playerListState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val querySnapshot = firestore.collection("players").get().await()

                val players = querySnapshot.documents.mapNotNull { document ->
                    try {
                        val player = document.toObject(Player::class.java)
                        player?.copy(id = document.id)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing player document", e)
                        null
                    }
                }

                // Convert Players to PlayerListItems with team names
                val playerListItems = mutableListOf<PlayerListItem>()

                for (player in players) {
                    // Get team name if player is on a team
                    var teamName = ""

                    if (player.teamId.isNotBlank()) {
                        // Check cache first
                        val cachedTeam = _teamsCache[player.teamId]

                        if (cachedTeam != null) {
                            teamName = cachedTeam.name
                        } else {
                            try {
                                val teamDoc = firestore.collection("teams").document(player.teamId).get().await()
                                if (teamDoc.exists()) {
                                    val team = teamDoc.toObject(Team::class.java)
                                    teamName = team?.name ?: "Unknown Team"

                                    // Cache the team
                                    if (team != null) {
                                        _teamsCache[player.teamId] = team
                                    }
                                }
                            } catch (e: Exception) {
                                Log.w(TAG, "Error fetching team details", e)
                                teamName = "Unknown Team"
                            }
                        }
                    }

                    // Calculate age
                    val age = if (player.dateOfBirth != Date(0)) {
                        val today = Date()
                        val diff = today.time - player.dateOfBirth.time
                        (diff / (1000L * 60 * 60 * 24 * 365.25)).toInt()
                    } else {
                        0
                    }

                    // Create PlayerListItem
                    val playerListItem = PlayerListItem(
                        id = player.id,
                        name = player.name,
                        position = player.position,
                        teamName = teamName,
                        jerseyNumber = player.jerseyNumber,
                        age = age,
                        hockeyType = player.hockeyType,
                        contactEmail = player.email,
                        contactPhone = player.contactNumber,
                        experienceYears = 0, // Not stored in Player model
                        rating = 0f // Not stored in Player model
                    )

                    playerListItems.add(playerListItem)
                }

                _playerListState.update {
                    it.copy(
                        isLoading = false,
                        players = players,
                        playerListItems = playerListItems
                    )
                }

                Log.d(TAG, "Loaded ${players.size} players")

            } catch (e: Exception) {
                Log.e(TAG, "Error loading players", e)
                _playerListState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load players: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Load players by hockey type
     */
    fun loadPlayersByHockeyType(hockeyType: HockeyType) {
        _playerListState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val querySnapshot = firestore.collection("players")
                    .whereEqualTo("hockeyType", hockeyType.name)
                    .get()
                    .await()

                val players = querySnapshot.documents.mapNotNull { document ->
                    try {
                        val player = document.toObject(Player::class.java)
                        player?.copy(id = document.id)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing player document", e)
                        null
                    }
                }

                // Convert to PlayerListItems (same as in loadAllPlayers)
                // ...similar code to loadAllPlayers for converting to PlayerListItems...
                val playerListItems = mutableListOf<PlayerListItem>()

                for (player in players) {
                    // Get team name if player is on a team
                    var teamName = ""

                    if (player.teamId.isNotBlank()) {
                        // Check cache first
                        val cachedTeam = _teamsCache[player.teamId]

                        if (cachedTeam != null) {
                            teamName = cachedTeam.name
                        } else {
                            try {
                                val teamDoc = firestore.collection("teams").document(player.teamId).get().await()
                                if (teamDoc.exists()) {
                                    val team = teamDoc.toObject(Team::class.java)
                                    teamName = team?.name ?: "Unknown Team"

                                    // Cache the team
                                    if (team != null) {
                                        _teamsCache[player.teamId] = team
                                    }
                                }
                            } catch (e: Exception) {
                                Log.w(TAG, "Error fetching team details", e)
                                teamName = "Unknown Team"
                            }
                        }
                    }

                    // Calculate age
                    val age = if (player.dateOfBirth != Date(0)) {
                        val today = Date()
                        val diff = today.time - player.dateOfBirth.time
                        (diff / (1000L * 60 * 60 * 24 * 365.25)).toInt()
                    } else {
                        0
                    }

                    // Create PlayerListItem
                    val playerListItem = PlayerListItem(
                        id = player.id,
                        name = player.name,
                        position = player.position,
                        teamName = teamName,
                        jerseyNumber = player.jerseyNumber,
                        age = age,
                        hockeyType = player.hockeyType,
                        contactEmail = player.email,
                        contactPhone = player.contactNumber,
                        experienceYears = 0, // Not stored in Player model
                        rating = 0f // Not stored in Player model
                    )

                    playerListItems.add(playerListItem)
                }

                _playerListState.update {
                    it.copy(
                        isLoading = false,
                        players = players,
                        playerListItems = playerListItems
                    )
                }

                Log.d(TAG, "Loaded ${players.size} players for hockey type: $hockeyType")

            } catch (e: Exception) {
                Log.e(TAG, "Error loading players by hockey type", e)
                _playerListState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load players: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Load players by team
     */
    fun loadPlayersByTeam(teamId: String) {
        _playerListState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val querySnapshot = firestore.collection("players")
                    .whereEqualTo("teamId", teamId)
                    .get()
                    .await()

                val players = querySnapshot.documents.mapNotNull { document ->
                    try {
                        val player = document.toObject(Player::class.java)
                        player?.copy(id = document.id)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing player document", e)
                        null
                    }
                }

                // Get team details
                var teamName = "Unknown Team"
                try {
                    // Check cache first
                    val cachedTeam = _teamsCache[teamId]

                    if (cachedTeam != null) {
                        teamName = cachedTeam.name
                    } else {
                        val teamDoc = firestore.collection("teams").document(teamId).get().await()
                        if (teamDoc.exists()) {
                            val team = teamDoc.toObject(Team::class.java)
                            teamName = team?.name ?: "Unknown Team"

                            // Cache the team
                            if (team != null) {
                                _teamsCache[teamId] = team
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error fetching team details", e)
                }

                // Convert to PlayerListItems
                val playerListItems = players.map { player ->
                    // Calculate age
                    val age = if (player.dateOfBirth != Date(0)) {
                        val today = Date()
                        val diff = today.time - player.dateOfBirth.time
                        (diff / (1000L * 60 * 60 * 24 * 365.25)).toInt()
                    } else {
                        0
                    }

                    PlayerListItem(
                        id = player.id,
                        name = player.name,
                        position = player.position,
                        teamName = teamName,
                        jerseyNumber = player.jerseyNumber,
                        age = age,
                        hockeyType = player.hockeyType,
                        contactEmail = player.email,
                        contactPhone = player.contactNumber,
                        experienceYears = 0, // Not stored in Player model
                        rating = 0f // Not stored in Player model
                    )
                }

                _playerListState.update {
                    it.copy(
                        isLoading = false,
                        players = players,
                        playerListItems = playerListItems
                    )
                }

                Log.d(TAG, "Loaded ${players.size} players for team: $teamId")

            } catch (e: Exception) {
                Log.e(TAG, "Error loading players by team", e)
                _playerListState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load team players: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Create a default Player object with empty fields
     */
    fun createEmptyPlayer(): Player {
        return Player(
            id = "",
            userId = authRepository.getCurrentUserId() ?: "",
            name = "",
            hockeyType = HockeyType.OUTDOOR,
            dateOfBirth = Date(),
            position = "",
            jerseyNumber = 0,
            teamId = "",
            contactNumber = "",
            email = "",
            photoUrl = "",
            stats = PlayerStats()
        )
    }

    /**
     * Reset player state
     */
    fun resetPlayerState() {
        _playerState.update { PlayerState() }
    }

    /**
     * Helper function to update a team's players list
     */
    private suspend fun updateTeamPlayers(teamId: String, playerId: String, addPlayer: Boolean) {
        try {
            val teamDoc = firestore.collection("teams").document(teamId).get().await()

            if (teamDoc.exists()) {
                val team = teamDoc.toObject(Team::class.java)

                if (team != null) {
                    val updatedPlayers = if (addPlayer) {
                        // Add player if not already in the list
                        if (!team.players.contains(playerId)) {
                            team.players + playerId
                        } else {
                            team.players
                        }
                    } else {
                        // Remove player from the list
                        team.players.filter { it != playerId }
                    }

                    // Update the team document
                    firestore.collection("teams").document(teamId)
                        .update("players", updatedPlayers)
                        .await()

                    // Update local cache
                    _teamsCache[teamId] = team.copy(players = updatedPlayers)

                    Log.d(TAG, "Updated team players: $teamId")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating team players", e)
            throw e
        }
    }

    /**
     * Helper function to convert Player to Map for Firestore
     */
    private fun playerToMap(player: Player): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        // Don't include id in the map, as it's the document ID
        map["userId"] = player.userId
        map["name"] = player.name
        map["hockeyType"] = player.hockeyType.name
        map["dateOfBirth"] = player.dateOfBirth
        map["position"] = player.position
        map["jerseyNumber"] = player.jerseyNumber
        map["teamId"] = player.teamId
        map["contactNumber"] = player.contactNumber
        map["email"] = player.email
        map["photoUrl"] = player.photoUrl

        // Convert PlayerStats to Map
        val statsMap = mutableMapOf<String, Any>()
        statsMap["goalsScored"] = player.stats.goalsScored
        statsMap["assists"] = player.stats.assists
        statsMap["gamesPlayed"] = player.stats.gamesPlayed
        statsMap["yellowCards"] = player.stats.yellowCards
        statsMap["redCards"] = player.stats.redCards

        map["stats"] = statsMap

        return map
    }
}