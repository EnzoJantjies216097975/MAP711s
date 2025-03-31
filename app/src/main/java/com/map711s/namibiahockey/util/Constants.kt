package com.map711s.namibiahockey.util

/**
 * Application-wide constants
 */
object Constants {

    // API endpoints
    object Api {
        const val BASE_URL = "https://api.namibiahockey.org/"
        const val API_VERSION = "v1"
        const val TIMEOUT_SECONDS = 30L

        // API endpoints
        const val LOGIN = "auth/login"
        const val REGISTER = "auth/register"
        const val FORGOT_PASSWORD = "auth/forgot-password"
        const val USER_PROFILE = "user/profile"
        const val TEAMS = "teams"
        const val PLAYERS = "players"
        const val EVENTS = "events"
        const val NEWS = "news"
    }

    // Shared Preferences keys
    object Prefs {
        const val PREF_NAME = "namibia_hockey_prefs"
        const val KEY_AUTH_TOKEN = "auth_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_DARK_MODE = "dark_mode"
        const val KEY_NOTIFICATION_ENABLED = "notifications_enabled"
        const val KEY_LAST_SYNC = "last_sync_timestamp"
    }

    // Database
    object Database {
        val DB_NAME = "namibia_hockey_db"
        val DB_VERSION = 1

        // Table names
        const val TABLE_USERS = "users"
        const val TABLE_TEAMS = "teams"
        const val TABLE_PLAYERS = "players"
        const val TABLE_EVENTS = "events"
        const val TABLE_NEWS = "news"
    }

    // Navigation routes
    object Routes {
        // Auth routes
        const val LOGIN = "login"
        const val REGISTER = "register"
        const val FORGOT_PASSWORD = "forgot_password"

        // Main routes
        const val DASHBOARD = "dashboard"
        const val TEAMS = "teams"
        const val TEAM_DETAIL = "team_detail/{teamId}"
        const val TEAM_REGISTRATION = "team_registration"
        const val TEAM_EDIT = "team_edit/{teamId}"

        const val PLAYERS = "players"
        const val PLAYER_DETAIL = "player_detail/{playerId}"
        const val PLAYER_REGISTRATION = "player_registration"
        const val PLAYER_EDIT = "player_edit/{playerId}"

        const val EVENTS = "events"
        const val EVENT_DETAIL = "event_detail/{eventId}"
        const val EVENT_REGISTRATION = "event_registration/{eventId}"

        const val SETTINGS = "settings"

        // Dynamic route generation
        fun teamDetail(teamId: String) = "team_detail/$teamId"
        fun teamEdit(teamId: String) = "team_edit/$teamId"
        fun playerDetail(playerId: String) = "player_detail/$playerId"
        fun playerEdit(playerId: String) = "player_edit/$playerId"
        fun eventDetail(eventId: String) = "event_detail/$eventId"
        fun eventRegistration(eventId: String) = "event_registration/$eventId"
    }

    // App settings
    object Settings {
        const val MIN_PASSWORD_LENGTH = 8
        const val MINIMUM_TEAM_PLAYERS = 11
        const val MAXIMUM_TEAM_PLAYERS = 22

        // Team divisions
        val TEAM_DIVISIONS = listOf(
            "Men's Premier",
            "Women's Premier",
            "Men's First",
            "Women's First",
            "Junior (U18)",
            "Junior (U16)",
            "Junior (U14)"
        )

        // Player positions
        val PLAYER_POSITIONS = listOf(
            "Forward",
            "Midfielder",
            "Defender",
            "Goalkeeper"
        )

        // Event types
        val EVENT_TYPES = listOf(
            "Tournament",
            "League Match",
            "Training",
            "Friendly"
        )
    }

    // Error messages
    object ErrorMessages {
        const val GENERAL_ERROR = "Something went wrong. Please try again later."
        const val NETWORK_ERROR = "Network error. Please check your internet connection."
        const val LOGIN_ERROR = "Invalid username or password."
        const val REGISTRATION_ERROR = "Registration failed. Please try again."
        const val TEAM_REGISTRATION_ERROR = "Team registration failed. Please try again."
        const val PLAYER_REGISTRATION_ERROR = "Player registration failed. Please try again."
        const val UNAUTHORIZED = "Unauthorized. Please log in again."
        const val SERVER_ERROR = "Server error. Please try again later."
    }

    // Validation regex patterns
    object ValidationPatterns {
        const val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        const val PHONE_PATTERN = "^[+]?[0-9]{10,13}$"
        const val NAME_PATTERN = "^[A-Za-z\\s'-]{2,50}$"
    }

    // Date formats
    object DateFormats {
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
        const val DATE_DISPLAY_FORMAT = "dd MMM yyyy"
        const val TIME_DISPLAY_FORMAT = "HH:mm"
    }

    // Media content
    object Media {
        const val MAX_IMAGE_SIZE_MB = 5
        const val AVATAR_DIMENSION_PX = 512
        const val TEAM_LOGO_DIMENSION_PX = 512
        const val COMPRESSION_QUALITY = 80
    }
}