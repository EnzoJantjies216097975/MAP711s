package com.map711s.namibiahockey.presentation.team.state

import com.map711s.namibiahockey.data.model.Team

// State for a list of Teams
data class TeamListState(
    val isLoading: Boolean = false,
    val teams: List<Team> = emptyList(),
    val error: String? = null
)