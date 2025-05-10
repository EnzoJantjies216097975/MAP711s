package com.map711s.namibiahockey.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val phone: String,
    val role: UserRole
)

enum class UserRole {
    ADMIN,
    COACH,
    MANAGER,
    PLAYER
}