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
    object TeamRegistration : Screen("team_registration")

    // Screens with parameters
    object EventDetails : Screen("event_details/{eventId}") {
        fun createRoute(eventId: String) = "event_details/$eventId"
    }

    object PlayerDetails : Screen("player_details/{playerId}") {
        fun createRoute(playerId: String) = "player_details/$playerId"
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