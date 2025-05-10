package com.map711s.namibiahockey.presentation.auth

data class LoginState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userId: String = "",
    val error: String? = null,
    val message: String? = null
)