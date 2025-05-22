package com.map711s.namibiahockey.data.model

data class PlayerProfile(
    val id: String,
    val name: String,
    val position: String,
    val jerseyNumber: Int,
    val teamName: String,
    val teamId: String,
    val hockeyType: HockeyType,
    val age: Int,
    val nationality: String = "Namibian",
    val stats: PlayerProfileStats,
    val isNationalPlayer: Boolean = false,
    val photoUrl: String = ""
)