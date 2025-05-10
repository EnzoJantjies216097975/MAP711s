package com.map711s.namibiahockey.presentation.auth

import com.map711s.namibiahockey.domain.model.User

data class UserProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)