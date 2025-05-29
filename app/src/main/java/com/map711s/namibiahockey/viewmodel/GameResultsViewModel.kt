package com.map711s.namibiahockey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.LiveGame
import com.map711s.namibiahockey.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameResultsViewModel @Inject constructor(
    private val gameResultsRepository: GameResultsRepository,
    private val teamRepository: TeamRepository
) : ViewModel() {

    fun processGameResult(game: LiveGame) {
        viewModelScope.launch {
            // Update team statistics
            updateTeamStatistics(game.team1Id, game.team1Score, game.team2Score)
            updateTeamStatistics(game.team2Id, game.team2Score, game.team1Score)

            // Save game result
            val result = GameResult(
                gameId = game.id,
                team1Id = game.team1Id,
                team2Id = game.team2Id,
                team1Score = game.team1Score,
                team2Score = game.team2Score,
                date = game.startTime,
                venue = game.venue,
                hockeyType = game.hockeyType
            )

            gameResultsRepository.saveGameResult(result)
        }
    }

    private suspend fun updateTeamStatistics(teamId: String, goalsFor: Int, goalsAgainst: Int) {
        val team = teamRepository.getTeam(teamId).getOrNull() ?: return

        val updatedStats = team.statistics.copy(
            gamesPlayed = team.statistics.gamesPlayed + 1,
            goalsFor = team.statistics.goalsFor + goalsFor,
            goalsAgainst = team.statistics.goalsAgainst + goalsAgainst,
            wins = if (goalsFor > goalsAgainst) team.statistics.wins + 1 else team.statistics.wins,
            losses = if (goalsFor < goalsAgainst) team.statistics.losses + 1 else team.statistics.losses,
            draws = if (goalsFor == goalsAgainst) team.statistics.draws + 1 else team.statistics.draws
        )

        // Calculate points (assuming 3 for win, 1 for draw, 0 for loss)
        val points = updatedStats.wins * 3 + updatedStats.draws

        teamRepository.updateTeam(team.copy(statistics = updatedStats.copy(points = points)))
    }
}