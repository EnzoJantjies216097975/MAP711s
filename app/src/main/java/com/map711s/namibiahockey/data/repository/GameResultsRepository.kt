package com.map711s.namibiahockey.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.GameResult
import com.map711s.namibiahockey.data.model.GameStatus
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.TeamSeasonStats
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameResultsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val gameResultsCollection = firestore.collection("game_results")
    private val teamStatsCollection = firestore.collection("team_season_stats")
    private val TAG = "GameResultsRepository"

    // Save game result
    suspend fun saveGameResult(gameResult: GameResult): Result<String> {
        return try {
            Log.d(TAG, "Saving game result: ${gameResult.team1Name} vs ${gameResult.team2Name}")

            val documentReference = gameResultsCollection.add(gameResult.toHashMap()).await()

            Log.d(TAG, "Game result saved with ID: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving game result", e)
            Result.failure(e)
        }
    }

    // Get game results for an event
    suspend fun getGameResultsForEvent(eventId: String): Result<List<GameResult>> {
        return try {
            Log.d(TAG, "Fetching game results for event: $eventId")

            val querySnapshot = gameResultsCollection
                .whereEqualTo("eventId", eventId)
                .get()
                .await()

            val gameResults = querySnapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data ?: return@mapNotNull null
                    mapDocumentToGameResult(document.id, data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping game result document", e)
                    null
                }
            }

            Log.d(TAG, "Fetched ${gameResults.size} game results for event $eventId")
            Result.success(gameResults)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching game results for event", e)
            Result.failure(e)
        }
    }

    // Get game results for a team
    suspend fun getGameResultsForTeam(teamId: String): Result<List<GameResult>> {
        return try {
            Log.d(TAG, "Fetching game results for team: $teamId")

            val querySnapshot1 = gameResultsCollection
                .whereEqualTo("team1Id", teamId)
                .get()
                .await()

            val querySnapshot2 = gameResultsCollection
                .whereEqualTo("team2Id", teamId)
                .get()
                .await()

            val gameResults = mutableListOf<GameResult>()

            // Add results where team was team1
            querySnapshot1.documents.forEach { document ->
                try {
                    val data = document.data ?: return@forEach
                    val gameResult = mapDocumentToGameResult(document.id, data)
                    gameResults.add(gameResult)
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping game result document", e)
                }
            }

            // Add results where team was team2
            querySnapshot2.documents.forEach { document ->
                try {
                    val data = document.data ?: return@forEach
                    val gameResult = mapDocumentToGameResult(document.id, data)
                    gameResults.add(gameResult)
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping game result document", e)
                }
            }

            // Sort by date descending
            val sortedResults = gameResults.sortedByDescending { it.gameDate }

            Log.d(TAG, "Fetched ${sortedResults.size} game results for team $teamId")
            Result.success(sortedResults)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching game results for team", e)
            Result.failure(e)
        }
    }

    // Get all game results
    suspend fun getAllGameResults(): Result<List<GameResult>> {
        return try {
            Log.d(TAG, "Fetching all game results")

            val querySnapshot = gameResultsCollection
                .orderBy("gameDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val gameResults = querySnapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data ?: return@mapNotNull null
                    mapDocumentToGameResult(document.id, data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping game result document", e)
                    null
                }
            }

            Log.d(TAG, "Fetched ${gameResults.size} total game results")
            Result.success(gameResults)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching all game results", e)
            Result.failure(e)
        }
    }

    // Save team season statistics
    suspend fun saveTeamSeasonStats(teamStats: TeamSeasonStats): Result<String> {
        return try {
            Log.d(TAG, "Saving team season stats for: ${teamStats.teamName}")

            val documentReference = teamStatsCollection.add(teamStats.toHashMap()).await()

            Log.d(TAG, "Team season stats saved with ID: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving team season stats", e)
            Result.failure(e)
        }
    }

    // Get team season statistics
    suspend fun getTeamSeasonStats(season: String = "2024"): Result<List<TeamSeasonStats>> {
        return try {
            Log.d(TAG, "Fetching team season stats for season: $season")

            val querySnapshot = teamStatsCollection
                .whereEqualTo("season", season)
                .orderBy("points", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val teamStats = querySnapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data ?: return@mapNotNull null
                    mapDocumentToTeamSeasonStats(document.id, data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping team season stats document", e)
                    null
                }
            }

            Log.d(TAG, "Fetched ${teamStats.size} team season stats")
            Result.success(teamStats)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching team season stats", e)
            Result.failure(e)
        }
    }

    // Update game result
    suspend fun updateGameResult(gameResult: GameResult): Result<Unit> {
        return try {
            Log.d(TAG, "Updating game result: ${gameResult.id}")

            gameResultsCollection.document(gameResult.id)
                .set(gameResult.toHashMap())
                .await()

            Log.d(TAG, "Game result updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating game result", e)
            Result.failure(e)
        }
    }

    // Delete game result
    suspend fun deleteGameResult(gameResultId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Deleting game result: $gameResultId")

            gameResultsCollection.document(gameResultId).delete().await()

            Log.d(TAG, "Game result deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting game result", e)
            Result.failure(e)
        }
    }

    // Helper function to map Firestore document to GameResult
    private fun mapDocumentToGameResult(documentId: String, data: Map<String, Any>): GameResult {
        // Map team1Stats
        val team1StatsMap = data["team1Stats"] as? Map<String, Any> ?: emptyMap()
        val team1Stats = com.map711s.namibiahockey.data.model.TeamGameStats(
            possession = (team1StatsMap["possession"] as? Long)?.toInt() ?: 0,
            shots = (team1StatsMap["shots"] as? Long)?.toInt() ?: 0,
            shotsOnTarget = (team1StatsMap["shotsOnTarget"] as? Long)?.toInt() ?: 0,
            corners = (team1StatsMap["corners"] as? Long)?.toInt() ?: 0,
            fouls = (team1StatsMap["fouls"] as? Long)?.toInt() ?: 0,
            yellowCards = (team1StatsMap["yellowCards"] as? Long)?.toInt() ?: 0,
            redCards = (team1StatsMap["redCards"] as? Long)?.toInt() ?: 0,
            saves = (team1StatsMap["saves"] as? Long)?.toInt() ?: 0
        )

        // Map team2Stats
        val team2StatsMap = data["team2Stats"] as? Map<String, Any> ?: emptyMap()
        val team2Stats = com.map711s.namibiahockey.data.model.TeamGameStats(
            possession = (team2StatsMap["possession"] as? Long)?.toInt() ?: 0,
            shots = (team2StatsMap["shots"] as? Long)?.toInt() ?: 0,
            shotsOnTarget = (team2StatsMap["shotsOnTarget"] as? Long)?.toInt() ?: 0,
            corners = (team2StatsMap["corners"] as? Long)?.toInt() ?: 0,
            fouls = (team2StatsMap["fouls"] as? Long)?.toInt() ?: 0,
            yellowCards = (team2StatsMap["yellowCards"] as? Long)?.toInt() ?: 0,
            redCards = (team2StatsMap["redCards"] as? Long)?.toInt() ?: 0,
            saves = (team2StatsMap["saves"] as? Long)?.toInt() ?: 0
        )

        return GameResult(
            id = documentId,
            eventId = data["eventId"] as? String ?: "",
            eventName = data["eventName"] as? String ?: "",
            team1Id = data["team1Id"] as? String ?: "",
            team1Name = data["team1Name"] as? String ?: "",
            team1Score = (data["team1Score"] as? Long)?.toInt() ?: 0,
            team2Id = data["team2Id"] as? String ?: "",
            team2Name = data["team2Name"] as? String ?: "",
            team2Score = (data["team2Score"] as? Long)?.toInt() ?: 0,
            gameDate = (data["gameDate"] as? Timestamp)?.toDate() ?: Date(),
            venue = data["venue"] as? String ?: "",
            status = try {
                GameStatus.valueOf(
                    data["status"] as? String ?: "COMPLETED"
                )
            } catch (e: Exception) {
                GameStatus.COMPLETED
            },
            team1Stats = team1Stats,
            team2Stats = team2Stats,
            notablePlayerId = data["notablePlayerId"] as? String ?: "",
            notablePlayerName = data["notablePlayerName"] as? String ?: "",
            notablePlayerReason = data["notablePlayerReason"] as? String ?: ""
        )
    }

    // Helper function to map Firestore document to TeamSeasonStats
    private fun mapDocumentToTeamSeasonStats(documentId: String, data: Map<String, Any>): TeamSeasonStats {
        return TeamSeasonStats(
            id = documentId,
            teamName = data["teamName"] as? String ?: "",
            teamId = data["teamId"] as? String ?: "",
            season = data["season"] as? String ?: "",
            position = (data["position"] as? Long)?.toInt() ?: 0,
            gamesPlayed = (data["gamesPlayed"] as? Long)?.toInt() ?: 0,
            wins = (data["wins"] as? Long)?.toInt() ?: 0,
            draws = (data["draws"] as? Long)?.toInt() ?: 0,
            losses = (data["losses"] as? Long)?.toInt() ?: 0,
            goalsFor = (data["goalsFor"] as? Long)?.toInt() ?: 0,
            goalsAgainst = (data["goalsAgainst"] as? Long)?.toInt() ?: 0,
            goalDifference = (data["goalDifference"] as? Long)?.toInt() ?: 0,
            totalPoints = (data["totalPoints"] as? Long)?.toInt() ?: 0,
            bonusPoints = (data["bonusPoints"] as? Long)?.toInt() ?: 0,
            hockeyType = try {
                HockeyType.valueOf(data["hockeyType"] as? String ?: "OUTDOOR")
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }
        )
    }
}