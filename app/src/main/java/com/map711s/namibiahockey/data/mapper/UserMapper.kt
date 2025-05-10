package com.map711s.namibiahockey.data.mapper

import com.map711s.namibiahockey.data.remote.model.FirebaseUser
import com.map711s.namibiahockey.domain.model.User
import com.map711s.namibiahockey.domain.model.UserRole


// Domain to Data
fun User.toData(): FirebaseUser {
    return FirebaseUser(
        id = id,
        email = email,
        name = name,
        phone = phone,
        role = role.name
    )
}

fun FirebaseUser.toDomain(): User {
    return User(
        id = id,
        email = email,
        name = name,
        phone = phone,
        role = try {
            UserRole.valueOf(role)
        } catch (e: IllegalArgumentException) {
            UserRole.PLAYER // Default to PLAYER if role doesn't match
        }
    )
}