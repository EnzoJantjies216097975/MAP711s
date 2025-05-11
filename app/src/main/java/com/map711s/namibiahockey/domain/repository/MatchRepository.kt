package com.map711s.namibiahockey.domain.repository

import com.map711s.namibiahockey.data.model.Card
import com.map711s.namibiahockey.data.model.Match
import com.map711s.namibiahockey.data.model.MatchStatus
import com.map711s.namibiahockey.data.model.Scorer
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface MatchRepository {
    /**
     * Create a new match
     * @param match The match to create
     * @return Result containing the ID of the created match or an error
     */
    suspend fun createMatch(match: Match): Result<String>

    /**
     * Get a match by ID
     * @param matchId ID of the match to retrieve
     * @return Result containing the match or an error
     */
    suspend fun getMatch(matchId: String): Result<Match>

    /**
     * Update an existing match
     * @param match The match with updated data
     * @return Result indicating success or failure
     */
    suspend fun updateMatch(match: Match): Result<Unit>

    /**
     * Delete a match by ID
     * @param matchId ID of the match to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteMatch(matchId: String): Result<Unit>

    /**
     * Get all matches
     * @return Result containing a list of all matches or an error
     */
    suspend fun getAllMatches(): Result<List<Match>>

    /**
     * Get matches as a Flow
     * @return Flow of matches list that updates when data changes
     */
    fun getMatchesFlow(): Flow<List<Match>>

    /**
     * Get matches by event
     * @param eventId The event ID to filter by
     * @return Result containing a list of matches in the specified event
     */
    suspend fun getMatchesByEvent(eventId: String): Result<List<Match>>

    /**
     * Get matches by team
     * @param teamId The team ID to filter by
     * @return Result containing a list of matches for the specified team
     */
    suspend fun getMatchesByTeam(teamId: String): Result<List<Match>>

    /**
     * Get matches by date range
     * @param startDate The start date
     * @param endDate The end date
     * @return Result containing a list of matches within the date range
     */
    suspend fun getMatchesByDateRange(startDate: Date, endDate: Date): Result<List<Match>>

    /**
     * Get matches by status
     * @param status The match status to filter by
     * @return Result containing a list of matches with the specified status
     */
    suspend fun getMatchesByStatus(status: MatchStatus): Result<List<Match>>

    /**
     * Update match score
     * @param matchId ID of the match
     * @param homeScore Home team score
     * @param awayScore Away team score
     * @return Result indicating success or failure
     */
    suspend fun updateMatchScore(matchId: String, homeScore: Int, awayScore: Int): Result<Unit>

    /**
     * Update match status
     * @param matchId ID of the match
     * @param status New match status
     * @return Result indicating success or failure
     */
    suspend fun updateMatchStatus(matchId: String, status: MatchStatus): Result<Unit>

    /**
     * Add scorer to match
     * @param matchId ID of the match
     * @param scorer The scorer details
     * @return Result indicating success or failure
     */
    suspend fun addScorer(matchId: String, scorer: Scorer): Result<Unit>

    /**
     * Add card to match
     * @param matchId ID of the match
     * @param card The card details
     * @return Result indicating success or failure
     */
    suspend fun addCard(matchId: String, card: Card): Result<Unit>

    /**
     * Get upcoming matches
     * @param limit Number of matches to retrieve
     * @return Result containing a list of upcoming matches
     */
    suspend fun getUpcomingMatches(limit: Int = 10): Result<List<Match>>

    /**
     * Get recent matches
     * @param limit Number of matches to retrieve
     * @return Result containing a list of recent matches
     */
    suspend fun getRecentMatches(limit: Int = 10): Result<List<Match>>
}