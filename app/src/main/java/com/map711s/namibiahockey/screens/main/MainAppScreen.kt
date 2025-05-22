package com.map711s.namibiahockey.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.navigation.BottomNavItem
import com.map711s.namibiahockey.navigation.BottomNavigationBar
import com.map711s.namibiahockey.navigation.MainNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    hockeyType: HockeyType,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddEvent: () -> Unit,
    onNavigateToAddNews: () -> Unit,
    onNavigateToTeamRegistration: () -> Unit
) {
    val navController = rememberNavController()

    // Bottom navigation items
    val bottomNavItems = listOf(
        BottomNavItem("Home", "main_home", Icons.Default.Home),
        BottomNavItem("Events", "main_events", Icons.Default.CalendarMonth),
        BottomNavItem("Teams", "main_teams", Icons.Default.Groups),
        BottomNavItem("News", "main_news", Icons.Default.Newspaper),
        BottomNavItem("Profile", "profile", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                items = bottomNavItems
            )
        }
    ) { paddingValues ->
        MainNavHost(
            navController = navController,
            paddingValues = paddingValues,
            hockeyType = hockeyType.name,
            onNavigateToProfile = onNavigateToProfile,
            onNavigateToAddEvent = onNavigateToAddEvent,
            onNavigateToAddNews = onNavigateToAddNews,
            onNavigateToTeamRegistration = onNavigateToTeamRegistration
        )
    }
}