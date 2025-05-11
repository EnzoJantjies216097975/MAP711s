package com.map711s.namibiahockey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.map711s.namibiahockey.data.model.Card
import com.map711s.namibiahockey.data.model.Match
import com.map711s.namibiahockey.data.model.MatchStatus
import com.map711s.namibiahockey.data.model.Scorer
import com.map711s.namibiahockey.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MatchRepository {

    private val matchesCollection = firestore.collection("matches")
    private val matchesFlow = MutableStateFlow<List<Match>>(emptyList())

    init {
        // Initialize the flow by loading data
        refreshMatches()
    }

    private fun refreshMatches() {
        matchesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            snapshot?.let { querySnapshot ->
                val matches = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Match::class.java)?.copy(id = document.id)
                }
                matchesFlow.value = matches
            }
        }
    }

    override suspend fun createMatch(match: Match): Result<String> {
        return try {
            val matchMap = match.toHashMap()
            val documentReference = matchesCollection.add(matchMap).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMatch(matchId: String): Result<Match> {
        return try {
            val documentSnapshot = matchesCollection.document(matchId).get().await()
            if (documentSnapshot.exists()) {
                val match = documentSnapshot.toObject(Match::class.java)
                    ?: return Result.failure(Exception("Failed to parse match data"))
                Result.success(match.copy(id = documentSnapshot.id))
            } else {
                Result.failure(Exception("Match not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMatch(match: Match): Result<Unit> {
        return try {
            val matchMap = match.toHashMap()
            matchesCollection.document(match.id).set(matchMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMatch(matchId: String): Result<Unit> {
        return try {
            matchesCollection.document(matchId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllMatches(): Result<List<Match>> {
        return try {
            val querySnapshot = matchesCollection
                .orderBy("matchDate", Query.Direction.DESCENDING)
                .get()
                .await()

            val matches = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Match::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(matches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMatchesFlow(): Flow<List<Match>> {
        return matchesFlow
    }

    override suspend fun getMatchesByEvent(eventId: String): Result<List<Match>> {
        return try {
            val querySnapshot = matchesCollection
                .whereEqualTo("eventId", eventId)
                .orderBy("matchDate", Query.Direction.ASCENDING)
                .get()
                .await()

            val matches = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Match::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(matches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMatchesByTeam(teamId: String): Result<List<Match>> {
        return try {
            // Firebase doesn't support OR queries directly, so we need to do two queries
            val homeMatches = matchesCollection
                .whereEqualTo("homeTeamId", teamId)
                .get()
                .await()
                .documents

            val awayMatches = matchesCollection
                .whereEqualTo("awayTeamId", teamId)
                .get()
                .await()
                .documents

            // Combine and convert results
            val allMatches = (homeMatches + awayMatches).mapNotNull { document ->
                try {
                    document.toObject(Match::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            // Sort by date (newest first)
            val sortedMatches = allMatches.sortedByDescending { it.matchDate }

            Result.success(sortedMatches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMatchesByDateRange(startDate: Date, endDate: Date): Result<List<Match>> {
        return try {
            val querySnapshot = matchesCollection
                .whereGreaterThanOrEqualTo("matchDate", startDate)
                .whereLessThanOrEqualTo("matchDate", endDate)
                .orderBy("matchDate", Query.Direction.ASCENDING)
                .get()
                .await()

            val matches = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Match::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(matches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMatchesByStatus(status: MatchStatus): Result<List<Match>> {
        return try {
            val querySnapshot = matchesCollection
                .whereEqualTo("status", status.name)
                .orderBy("matchDate", Query.Direction.DESCENDING)
                .get()
                .await()

            val matches = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Match::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(matches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMatchScore(matchId: String, homeScore: Int, awayScore: Int): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>(
                "homeTeamScore" to homeScore,
                "awayTeamScore" to awayScore
            )

            matchesCollection.document(matchId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMatchStatus(matchId: String, status: MatchStatus): Result<Unit> {
        return try {
            matchesCollection.document(matchId)
                .update("status", status.name)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addScorer(matchId: String, scorer: Scorer): Result<Unit> {
        return try {
            // First get the current match
            val match = getMatch(matchId).getOrNull()
                ?: return Result.failure(Exception("Match not found"))

            // Add the new scorer
            val updatedScorers = match.scorers.toMutableList().apply {
                add(scorer)
            }

            // Update the match
            matchesCollection.document(matchId)
                .update("scorers", updatedScorers.map { it.toHashMap() })
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addCard(matchId: String, card: Card): Result<Unit> {
        return try {
            // First get the current match
            val match = getMatch(matchId).getOrNull()
                ?: return Result.failure(Exception("Match not found"))

            // Add the new card
            val updatedCards = match.cards.toMutableList().apply {
                add(card)
            }

            // Update the match
            matchesCollection.document(matchId)
                .update("cards", updatedCards.map { it.toHashMap() })
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUpcomingMatches(limit: Int): Result<List<Match>> {
        return try {
            val currentDate = Date()

            val querySnapshot = matchesCollection
                .whereGreaterThanOrEqualTo("matchDate", currentDate)
                .orderBy("matchDate", Query.Direction.ASCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val matches = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Match::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(matches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecentMatches(limit: Int): Result<List<Match>> {
        return try {
            val currentDate = Date()

            val querySnapshot = matchesCollection
                .whereLessThanOrEqualTo("matchDate", currentDate)
                .whereEqualTo("status", MatchStatus.COMPLETED.name)
                .orderBy("matchDate", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val matches = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Match::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(matches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}