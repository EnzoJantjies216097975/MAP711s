package com.map711s.namibiahockey.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.map711s.namibiahockey.util.Constants.Routes

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Routes.DASHBOARD,
        title = "Dashboard",
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        route = Routes.TEAMS,
        title = "Teams",
        icon = Icons.Default.Group
    ),
    BottomNavItem(
        route = Routes.EVENTS,
        title = "Events",
        icon = Icons.Default.CalendarMonth
    ),
    BottomNavItem(
        route = Routes.PLAYERS,
        title = "Players",
        icon = Icons.Default.Person
    ),
    BottomNavItem(
        route = Routes.SETTINGS,
        title = "Settings",
        icon = Icons.Default.Settings
    )
)