package com.map711s.namibiahockey.data.mapper

import com.map711s.namibiahockey.data.remote.model.FirebaseUser
import com.map711s.namibiahockey.domain.model.User
import com.map711s.namibiahockey.domain.model.UserRole

import com.map711s.namibiahockey.data.model.User as DataUser
import com.map711s.namibiahockey.domain.model.User as DomainUser
import com.map711s.namibiahockey.data.model.UserRole as DataRole
import com.map711s.namibiahockey.domain.model.UserRole as DomainRole

// Domain to Data
fun User.toFirebaseUser(): FirebaseUser {
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

// Convert Data Model to Domain Model
fun DataUser.toDomain(): DomainUser {
    return DomainUser(
        id = id,
        email = email,
        name = name,
        phone = phone,
        role = role.toDomain()
    )
}

// Convert Domain Model to Data Model
fun DomainUser.toData(): DataUser {
    return DataUser(
        id = id,
        email = email,
        name = name,
        phone = phone,
        role = role.toData()
    )
}

// Convert UserRole Enum
fun DataRole.ToDomain(): DomainRole {
    return when (this) {
        DataRole.ADMIN -> DomainRole.ADMIN
        DataRole.COACH -> DomainRole.COACH
        DataRole.MANAGER -> DomainRole.MANAGER
        DataRole.PLAYER -> DomainRole.PLAYER
    }
}

fun DomainRole.toData(): DataRole {
    return when (this) {
        DomainRole.ADMIN -> DataRole.ADMIN
        DomainRole.COACH -> DataRole.COACH
        DomainRole.MANAGER -> DataRole.MANAGER
        DomainRole.PLAYER -> DataRole.PLAYER
    }
}