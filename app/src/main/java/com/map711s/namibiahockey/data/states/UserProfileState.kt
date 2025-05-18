package com.map711s.namibiahockey.data.states

import com.map711s.namibiahockey.data.model.User


// State classes for UI
data class UserProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)