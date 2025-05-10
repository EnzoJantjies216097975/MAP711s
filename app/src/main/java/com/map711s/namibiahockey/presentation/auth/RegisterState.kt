package com.map711s.namibiahockey.presentation.auth

data class RegisterState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val userId: String = "",
    val error: String? = null
)