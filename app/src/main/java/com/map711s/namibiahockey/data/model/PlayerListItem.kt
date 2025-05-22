package com.map711s.namibiahockey.data.model

data class PlayerListItem(
    val id: String,
    val name: String,
    val position: String,
    val teamName: String,
    val jerseyNumber: Int,
    val age: Int = 0,
    val nationality: String = "Namibian",
    val isNationalPlayer: Boolean = false,
    val hockeyType: HockeyType = HockeyType.OUTDOOR,
    val contactEmail: String = "",
    val contactPhone: String = "",
    val experienceYears: Int = 0,
    val rating: Float = 0.0f
)