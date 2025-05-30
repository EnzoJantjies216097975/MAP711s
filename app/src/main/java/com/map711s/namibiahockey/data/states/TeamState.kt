package com.map711s.namibiahockey.data.states

import com.map711s.namibiahockey.data.model.Team

// State for a single Team
data class TeamState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val team: Team? = null,
    val teamId: String? = null,
    val error: String? = null,
    val successMessage: String? = null
)

// State for a list of Team objects
data class TeamListState(
    val isLoading: Boolean = false,
    val teams: List<Team> = emptyList(),
    val error: String? = null,
    val myTeams: List<Team> = emptyList(),
    val nationalTeams: List<Team> = emptyList(),
    val clubTeams: List<Team> = emptyList()
)