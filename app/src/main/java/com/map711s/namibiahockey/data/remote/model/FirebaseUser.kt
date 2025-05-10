package com.map711s.namibiahockey.data.remote.model

data class FirebaseUser(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val role: String = "PLAYER"
)