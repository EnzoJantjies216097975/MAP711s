package com.map711s.namibiahockey.data.model

import java.util.Date

data class Match(
    val id: String = "",
    val homeTeamId: String = "",
    val awayTeamId: String = "",
    val eventId: String = "", // Reference to the tournament/league event
    val venue: String = "",
    val matchDate: Date = Date(),
    val startTime: String = "",
    val status: MatchStatus = MatchStatus.SCHEDULED,
    val homeTeamScore: Int = 0,
    val awayTeamScore: Int = 0,
    val matchOfficials: List<String> = emptyList(), // Officials assigned to the match
    val matchStats: MatchStats = MatchStats(),
    val scorers: List<Scorer> = emptyList(),
    val cards: List<Card> = emptyList()
) {
    fun toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "homeTeamId" to homeTeamId,
            "awayTeamId" to awayTeamId,
            "eventId" to eventId,
            "venue" to venue,
            "matchDate" to matchDate,
            "startTime" to startTime,
            "status" to status.name,
            "homeTeamScore" to homeTeamScore,
            "awayTeamScore" to awayTeamScore,
            "matchOfficials" to matchOfficials,
            "matchStats" to matchStats.toHashMap(),
            "scorers" to scorers.map { it.toHashMap() },
            "cards" to cards.map { it.toHashMap() }
        )
    }
}

enum class MatchStatus {
    SCHEDULED, LIVE, COMPLETED, POSTPONED, CANCELLED
}

data class MatchStats(
    val homePossession: Int = 0, // percentage
    val awayPossession: Int = 0, // percentage
    val homePenaltyCorners: Int = 0,
    val awayPenaltyCorners: Int = 0,
    val homeShotsOnGoal: Int = 0,
    val awayShotsOnGoal: Int = 0
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "homePossession" to homePossession,
            "awayPossession" to awayPossession,
            "homePenaltyCorners" to homePenaltyCorners,
            "awayPenaltyCorners" to awayPenaltyCorners,
            "homeShotsOnGoal" to homeShotsOnGoal,
            "awayShotsOnGoal" to awayShotsOnGoal
        )
    }
}

data class Scorer(
    val playerId: String = "",
    val teamId: String = "",
    val minute: Int = 0,
    val isOwnGoal: Boolean = false,
    val isPenalty: Boolean = false
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "playerId" to playerId,
            "teamId" to teamId,
            "minute" to minute,
            "isOwnGoal" to isOwnGoal,
            "isPenalty" to isPenalty
        )
    }
}

data class Card(
    val playerId: String = "",
    val teamId: String = "",
    val minute: Int = 0,
    val cardType: CardType = CardType.YELLOW,
    val reason: String = ""
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "playerId" to playerId,
            "teamId" to teamId,
            "minute" to minute,
            "cardType" to cardType.name,
            "reason" to reason
        )
    }
}

enum class CardType {
    GREEN, YELLOW, RED
}