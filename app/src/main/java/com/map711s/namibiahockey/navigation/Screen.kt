package com.map711s.namibiahockey.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally


// Define all navigation routes here.
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")


    // Main Navigation items
    object TeamRegistration : Screen("team_registration")
    object EventEntries : Screen("event_entries")
    object PlayerManagement : Screen("player_management")
    object NewsFeed : Screen("news_feed")
    object Profile : Screen("profile")

    // Add Event and News screens
    object AddEvent : Screen("add_event")
    object AddNews : Screen("add_news")

    // Screens with parameters
    object EventDetails : Screen("event_details/{eventId}") {
        fun createRoute(eventId: String) = "event_details/$eventId"
    }

    object PlayerDetails : Screen("player_details/{playerId}") {
        fun createRoute(playerId: String) = "player_details/$playerId"
    }

    // String constants for bottom navigation
    companion object {
        const val HOME = "home"
        const val TEAM_REGISTRATION = "team_registration"
        const val EVENT_ENTRIES = "event_entries"
        const val PLAYER_MANAGEMENT = "player_management"
        const val NEWS_FEED = "news_feed"
        const val PROFILE = "profile"
        const val ADD_EVENT = "add_event"
        const val ADD_NEWS = "add_news"
    }

    // Include transition animations with each route
    val enterTransition: EnterTransition
        get() = when(this) {
            is Home -> fadeIn() + slideInHorizontally()
            is Login, is Register -> fadeIn()
            else -> fadeIn()
        }

    val exitTransition: ExitTransition
        get() = when(this) {
            is Home -> fadeOut() + slideOutHorizontally()
            is Login, is Register -> fadeOut()
            else -> fadeOut()
        }
}