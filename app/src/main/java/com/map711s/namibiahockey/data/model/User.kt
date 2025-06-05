package com.map711s.namibiahockey.data.model

import java.util.Date

// User model for authentication and user profiles
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.PLAYER,

    // Personal Information
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: Date? = null,
    val gender: Gender = Gender.NOT_SPECIFIED,
    val nationality: String = "Namibian",
    val idNumber: String = "", // National ID or passport number
    val profilePictureUrl: String = "",

    // Address Information
    val address: Address = Address(),

    // Hockey-specific Information
    val hockeyType: HockeyType = HockeyType.OUTDOOR,
    val preferredPosition: String = "",
    val experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER,
    val currentTeamId: String = "",
    val previousTeams: List<String> = emptyList(),
    val yearsPlaying: Int = 0,

    // Emergency Contact
    val emergencyContact: EmergencyContact = EmergencyContact(),

    // Account Status
    val isActive: Boolean = true,
    val isVerified: Boolean = false,
    val emailVerified: Boolean = false,
    val phoneVerified: Boolean = false,
    val profileCompleted: Boolean = false,

    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val lastLoginAt: Date? = null,

    // Preferences
    val preferences: UserPreferences = UserPreferences(),

    // Additional Info
    val bio: String = "",
    val achievements: List<String> = emptyList(),
    val medicalNotes: String = "", // For coaches/managers to note any medical considerations
    val socialMedia: SocialMediaLinks = SocialMediaLinks()
) {

    // Helper function to get full name
    fun getFullName(): String {
        return when {
            firstName.isNotBlank() && lastName.isNotBlank() -> "$firstName $lastName"
            name.isNotBlank() -> name
            else -> "Unknown User"
        }
    }

    // Helper function to get display name (first name only)
    fun getDisplayName(): String {
        return when {
            firstName.isNotBlank() -> firstName
            name.isNotBlank() -> name.split(" ").firstOrNull() ?: name
            else -> "User"
        }
    }

    // Helper function to get initials
    fun getInitials(): String {
        val fullName = getFullName()
        return fullName.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
            .ifEmpty { "U" }
    }

    // Helper function to calculate age
    fun getAge(): Int? {
        dateOfBirth?.let { dob ->
            val now = Date()
            val diffInMillis = now.time - dob.time
            val ageInYears = (diffInMillis / (365.25 * 24 * 60 * 60 * 1000)).toInt()
            return if (ageInYears >= 0) ageInYears else null
        }
        return null
    }



    // Helper function to check if user has specific permission
    fun hasPermission(permission: Permission): Boolean {
        return when (role) {
            UserRole.ADMIN -> true // Admin has all permissions
            UserRole.COACH -> permission in listOf(
                Permission.VIEW_PLAYERS,
                Permission.MANAGE_TEAM,
                Permission.CREATE_EVENTS,
                Permission.VIEW_REPORTS
            )
            UserRole.MANAGER -> permission in listOf(
                Permission.VIEW_PLAYERS,
                Permission.MANAGE_TEAM,
                Permission.CREATE_EVENTS,
                Permission.MANAGE_REGISTRATIONS
            )
            UserRole.PLAYER -> permission in listOf(
                Permission.VIEW_EVENTS,
                Permission.REGISTER_FOR_EVENTS,
                Permission.VIEW_TEAMS
            )
        }
    }

    // Convert to HashMap for Firestore
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "email" to email,
            "name" to name,
            "phone" to phone,
            "role" to role.name,
            "firstName" to firstName,
            "lastName" to lastName,
            "dateOfBirth" to (dateOfBirth ?: Date()),
            "gender" to gender.name,
            "nationality" to nationality,
            "idNumber" to idNumber,
            "profilePictureUrl" to profilePictureUrl,
            "address" to address.toHashMap(),
            "hockeyType" to hockeyType.name,
            "preferredPosition" to preferredPosition,
            "experienceLevel" to experienceLevel.name,
            "currentTeamId" to currentTeamId,
            "previousTeams" to previousTeams,
            "yearsPlaying" to yearsPlaying,
            "emergencyContact" to emergencyContact.toHashMap(),
            "isActive" to isActive,
            "isVerified" to isVerified,
            "emailVerified" to emailVerified,
            "phoneVerified" to phoneVerified,
            "profileCompleted" to profileCompleted,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "lastLoginAt" to (lastLoginAt ?: Date()),
            "preferences" to preferences.toHashMap(),
            "bio" to bio,
            "achievements" to achievements,
            "medicalNotes" to medicalNotes,
            "socialMedia" to socialMedia.toHashMap()
        )
    }
}

// Supporting data classes
data class Address(
    val street: String = "",
    val city: String = "",
    val region: String = "",
    val postalCode: String = "",
    val country: String = "Namibia"
) {


    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "street" to street,
            "city" to city,
            "region" to region,
            "postalCode" to postalCode,
            "country" to country
        )
    }
}

data class EmergencyContact(
    val name: String = "",
    val relationship: String = "",
    val phone: String = "",
    val email: String = ""
) {
    fun isValid(): Boolean {
        return name.isNotBlank() && phone.isNotBlank()
    }

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "relationship" to relationship,
            "phone" to phone,
            "email" to email
        )
    }
}

data class UserPreferences(
    val notificationsEnabled: Boolean = true,
    val emailNotifications: Boolean = true,
    val smsNotifications: Boolean = false,
    val marketingEmails: Boolean = false,
    val language: String = "English",
    val theme: String = "System", // Light, Dark, System
    val dateFormat: String = "DD/MM/YYYY",
    val timeFormat: String = "24h" // 12h or 24h
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "notificationsEnabled" to notificationsEnabled,
            "emailNotifications" to emailNotifications,
            "smsNotifications" to smsNotifications,
            "marketingEmails" to marketingEmails,
            "language" to language,
            "theme" to theme,
            "dateFormat" to dateFormat,
            "timeFormat" to timeFormat
        )
    }
}

data class SocialMediaLinks(
    val facebook: String = "",
    val instagram: String = "",
    val twitter: String = "",
    val linkedin: String = ""
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "facebook" to facebook,
            "instagram" to instagram,
            "twitter" to twitter,
            "linkedin" to linkedin
        )
    }
}

// Enums
enum class Gender {
    MALE,
    FEMALE,
    OTHER,
    NOT_SPECIFIED
}

enum class ExperienceLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    PROFESSIONAL
}

enum class Permission {
    VIEW_PLAYERS,
    MANAGE_TEAM,
    CREATE_EVENTS,
    MANAGE_EVENTS,
    VIEW_REPORTS,
    MANAGE_USERS,
    MANAGE_REGISTRATIONS,
    VIEW_EVENTS,
    REGISTER_FOR_EVENTS,
    VIEW_TEAMS,
    CREATE_NEWS,
    MANAGE_NEWS
}