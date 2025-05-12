package com.map711s.namibiahockey.data.mapper

fun com.map711s.namibiahockey.domain.model.UserRole.toData(): com.map711s.namibiahockey.data.model.UserRole {
    return when(this) {
        com.map711s.namibiahockey.domain.model.UserRole.ADMIN -> com.map711s.namibiahockey.data.model.UserRole.ADMIN
        com.map711s.namibiahockey.domain.model.UserRole.COACH -> com.map711s.namibiahockey.data.model.UserRole.COACH
        com.map711s.namibiahockey.domain.model.UserRole.MANAGER -> com.map711s.namibiahockey.data.model.UserRole.MANAGER
        com.map711s.namibiahockey.domain.model.UserRole.PLAYER -> com.map711s.namibiahockey.data.model.UserRole.PLAYER
    }
}

fun com.map711s.namibiahockey.data.model.UserRole.toDomain(): com.map711s.namibiahockey.domain.model.UserRole {
    return when(this) {
        com.map711s.namibiahockey.data.model.UserRole.ADMIN -> com.map711s.namibiahockey.domain.model.UserRole.ADMIN
        com.map711s.namibiahockey.data.model.UserRole.COACH -> com.map711s.namibiahockey.domain.model.UserRole.COACH
        com.map711s.namibiahockey.data.model.UserRole.MANAGER -> com.map711s.namibiahockey.domain.model.UserRole.MANAGER
        com.map711s.namibiahockey.data.model.UserRole.PLAYER -> com.map711s.namibiahockey.domain.model.UserRole.PLAYER
    }
}