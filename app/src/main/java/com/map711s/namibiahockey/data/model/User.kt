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

    // Helper function to check if user can play in specific age category
    fun canPlayInAgeCategory(ageCategory: String): Boolean {
        val age = getAge() ?: return false
        return when (ageCategory.uppercase()) {
            "U14" -> age < 14
            "U16" -> age < 16
            "U18" -> age < 18
            "U21" -> age < 21
            "SENIOR" -> age >= 18
            else -> true
        }
    }

    // Helper function to check if profile is complete
    fun isProfileComplete(): Boolean {
        return name.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                dateOfBirth != null &&
                gender != Gender.NOT_SPECIFIED &&
                address.isValid() &&
                emergencyContact.isValid()
    }

    // Helper function to get profile completion percentage
    fun getProfileCompletionPercentage(): Int {
        var completed = 0
        val total = 12

        if (name.isNotBlank()) completed++
        if (email.isNotBlank()) completed++
        if (phone.isNotBlank()) completed++
        if (dateOfBirth != null) completed++
        if (gender != Gender.NOT_SPECIFIED) completed++
        if (nationality.isNotBlank()) completed++
        if (address.isValid()) completed++
        if (emergencyContact.isValid()) completed++
        if (preferredPosition.isNotBlank()) completed++
        if (profilePictureUrl.isNotBlank()) completed++
        if (bio.isNotBlank()) completed++
        if (idNumber.isNotBlank()) completed++

        return (completed * 100) / total
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

    companion object {
        // Common hockey positions
        val HOCKEY_POSITIONS = listOf(
            "Goalkeeper",
            "Right Back",
            "Left Back",
            "Centre Back",
            "Right Half",
            "Left Half",
            "Centre Half",
            "Right Wing",
            "Left Wing",
            "Inside Right",
            "Inside Left",
            "Centre Forward",
            "Striker",
            "Midfielder",
            "Defender"
        )

        // Validation function for user registration
        fun validateUserRegistration(
            name: String,
            email: String,
            phone: String,
            password: String,
            confirmPassword: String
        ): List<String> {
            val errors = mutableListOf<String>()

            if (name.isBlank()) errors.add("Name is required")
            if (name.length < 2) errors.add("Name must be at least 2 characters")

            if (email.isBlank()) errors.add("Email is required")
            if (!isValidEmail(email)) errors.add("Invalid email format")

            if (phone.isBlank()) errors.add("Phone number is required")
            if (!isValidPhoneNumber(phone)) errors.add("Invalid phone number format")

            if (password.isBlank()) errors.add("Password is required")
            if (password.length < 6) errors.add("Password must be at least 6 characters")

            if (password != confirmPassword) errors.add("Passwords do not match")

            return errors
        }

        // Validation function for profile update
        fun validateProfileUpdate(
            firstName: String,
            lastName: String,
            phone: String,
            dateOfBirth: Date?,
            emergencyContactName: String,
            emergencyContactPhone: String
        ): List<String> {
            val errors = mutableListOf<String>()

            if (firstName.isBlank()) errors.add("First name is required")
            if (lastName.isBlank()) errors.add("Last name is required")
            if (!isValidPhoneNumber(phone)) errors.add("Invalid phone number")
            if (dateOfBirth == null) errors.add("Date of birth is required")
            if (dateOfBirth != null && getAgeFromDate(dateOfBirth) < 5) {
                errors.add("Age must be at least 5 years")
            }
            if (emergencyContactName.isBlank()) errors.add("Emergency contact name is required")
            if (!isValidPhoneNumber(emergencyContactPhone)) {
                errors.add("Invalid emergency contact phone number")
            }

            return errors
        }

        private fun isValidEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        private fun isValidPhoneNumber(phone: String): Boolean {
            // Namibian phone number format: +264 followed by 8-9 digits
            val cleanPhone = phone.replace(Regex("[\\s-()]"), "")
            return cleanPhone.matches(Regex("^(\\+264|264)?[0-9]{8,9}$"))
        }

        private fun getAgeFromDate(dateOfBirth: Date): Int {
            val now = Date()
            val diffInMillis = now.time - dateOfBirth.time
            return (diffInMillis / (365.25 * 24 * 60 * 60 * 1000)).toInt()
        }
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
    fun isValid(): Boolean {
        return street.isNotBlank() && city.isNotBlank()
    }

    fun getFullAddress(): String {
        val parts = listOfNotNull(
            street.takeIf { it.isNotBlank() },
            city.takeIf { it.isNotBlank() },
            region.takeIf { it.isNotBlank() },
            postalCode.takeIf { it.isNotBlank() },
            country.takeIf { it.isNotBlank() }
        )
        return parts.joinToString(", ")
    }

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