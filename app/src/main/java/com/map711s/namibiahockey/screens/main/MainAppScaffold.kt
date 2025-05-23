package com.map711s.namibiahockey.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.navigation.BottomNavItem
import com.map711s.namibiahockey.navigation.BottomNavigationBar
import com.map711s.namibiahockey.navigation.Routes
import com.map711s.namibiahockey.screens.events.EventEntriesScreen
import com.map711s.namibiahockey.screens.home.HomeScreen
import com.map711s.namibiahockey.screens.newsfeed.NewsFeedScreen
import com.map711s.namibiahockey.screens.player.PlayerManagementScreen
import com.map711s.namibiahockey.screens.team.TeamsScreen

@Composable
fun MainAppScaffold(
    navController: NavHostController, // This is the main nav controller from parent
    hockeyType: HockeyType,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddEvent: () -> Unit,
    onNavigateToAddNews: () -> Unit,
    onNavigateToEventDetails: (String, HockeyType) -> Unit,
    onNavigateToNewsDetails: (String) -> Unit,
    onNavigateToTeamRegistration: () -> Unit,
    onNavigateToTeamDetails: (String) -> Unit,
    onSwitchHockeyType: (HockeyType) -> Unit
) {
    // Create a SEPARATE NavController for bottom navigation
    val bottomNavController = rememberNavController()

    val bottomNavItems = listOf(

        BottomNavItem(
            name = "News",
            route = Routes.BOTTOM_NEWS,
            icon = Icons.Default.Newspaper
        ),
        BottomNavItem(
            name = "Events",
            route = Routes.BOTTOM_EVENTS,
            icon = Icons.Default.CalendarMonth
        ),
        BottomNavItem(
            name = "Home",
            route = Routes.BOTTOM_HOME,
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            name = "Teams",
            route = Routes.BOTTOM_TEAMS,
            icon = Icons.Default.Groups
        ),
        BottomNavItem(
            name = "Players",
            route = Routes.BOTTOM_PLAYERS,
            icon = Icons.Default.Person
        )
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = bottomNavController, // Use the separate controller
                items = bottomNavItems
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController, // Use the separate controller
            startDestination = Routes.BOTTOM_HOME,
            modifier = Modifier.padding(paddingValues)
        ) {

            // Events screen
            composable(Routes.BOTTOM_EVENTS) {
                EventEntriesScreen(
                    hockeyType = hockeyType,
                    onNavigateBack = { }, // No back button in bottom nav
                    onNavigateToAddEvent = onNavigateToAddEvent,
                    onNavigateToEventDetails = onNavigateToEventDetails
                )
            }


            // News screen
            composable(Routes.BOTTOM_NEWS) {
                NewsFeedScreen(
                    hockeyType = hockeyType,
                    onNavigateBack = { }, // No back button in bottom nav
                    onNavigateToAddNews = onNavigateToAddNews,
                    onNavigateToNewsDetails = onNavigateToNewsDetails
                )
            }

            // Home screen
            composable(Routes.BOTTOM_HOME) {
                HomeScreen(
                    hockeyType = hockeyType,
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToEventDetails = onNavigateToEventDetails,
                    onNavigateToNewsDetails = onNavigateToNewsDetails,
                    onSwitchHockeyType = onSwitchHockeyType
                )
            }

            // Players screen
            composable(Routes.BOTTOM_PLAYERS) {
                PlayerManagementScreen(
                    hockeyType = hockeyType,
                    onNavigateBack = { } // No back button in bottom nav
                )
            }

            // Teams screen
            composable(Routes.BOTTOM_TEAMS) {
                TeamsScreen(
                    hockeyType = hockeyType,
                    onNavigateBack = { }, // No back button in bottom nav
                    onNavigateToCreateTeam = onNavigateToTeamRegistration,
                    onNavigateToTeamDetails = onNavigateToTeamDetails
                )
            }
        }
    }
}