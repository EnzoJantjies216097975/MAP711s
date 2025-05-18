package com.map711s.namibiahockey.data.states


// State classes for UI
data class RegisterState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val userId: String = "",
    val error: String? = null
)