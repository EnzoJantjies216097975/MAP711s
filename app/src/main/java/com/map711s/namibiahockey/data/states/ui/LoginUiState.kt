package com.map711s.namibiahockey.data.states.ui

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userId: String = "",
    val email: String = "",
    val password: String = "",
    val error: String? = null,
    val message: String? = null
)