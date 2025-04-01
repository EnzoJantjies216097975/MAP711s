package com.map711s.namibiahockey.data.model

import java.util.Date

data class Team(
    val id: String = "",
    val name: String = "",
    val category: String = "", // e.g., Men's, Women's, Junior
    val division: String = "", // e.g., Premier, First Division
    val coach: String = "",
    val manager: String = "",
    val players: List<String> = emptyList(), // List of player IDs
    val createdAt: Date = Date(),
    val logoUrl: String = ""
)