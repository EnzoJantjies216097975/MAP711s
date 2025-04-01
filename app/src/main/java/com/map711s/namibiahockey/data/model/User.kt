package com.map711s.namibiahockey.data.model

// User model for authentication and user profiles
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.PLAYER
)