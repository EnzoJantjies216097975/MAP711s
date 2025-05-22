package com.map711s.namibiahockey.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.screens.events.EventDetailsScreen
import com.map711s.namibiahockey.screens.events.EventEntriesScreen
import com.map711s.namibiahockey.screens.home.HomeScreen
import com.map711s.namibiahockey.screens.newsfeed.NewsDetailsScreen
import com.map711s.namibiahockey.screens.newsfeed.NewsFeedScreen
import com.map711s.namibiahockey.screens.player.PlayerManagementScreen
import com.map711s.namibiahockey.screens.profile.ProfileScreen
import com.map711s.namibiahockey.screens.team.TeamsScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    hockeyType: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddEvent: () -> Unit,
    onNavigateToAddNews: () -> Unit,
    onNavigateToTeamRegistration: () -> Unit
) {
    val parsedHockeyType = try {
        HockeyType.valueOf(hockeyType)
    } catch (e: Exception) {
        HockeyType.OUTDOOR
    }

    NavHost(
        navController = navController,
        startDestination = "main_home",
        modifier = Modifier.padding(paddingValues)
    ) {
        // Home screen with news and events
        composable("main_home") {
            HomeScreen(
                hockeyType = parsedHockeyType,
                navController = navController,
                onSwitchHockeyType = { /* Handle hockey type switch */ },
                onNavigateToTeamRegistration = onNavigateToTeamRegistration,
                onNavigateToEventEntries = { navController.navigate("main_events") },
                onNavigateToPlayerManagement = { navController.navigate("main_players") },
                onNavigateToNewsFeed = { navController.navigate("main_news") },
                onNavigateToProfile = onNavigateToProfile
            )
        }

        // Events screen
        composable("main_events") {
            EventEntriesScreen(
                hockeyType = parsedHockeyType,
                onNavigateBack = { navController.navigate("main_home") },
                onNavigateToAddEvent = onNavigateToAddEvent,
                onNavigateToEventDetails = { eventId, eventHockeyType ->
                    navController.navigate("event_details/${eventHockeyType.name}/$eventId")
                }
            )
        }

        // Teams screen
        composable("main_teams") {
            TeamsScreen(
                hockeyType = parsedHockeyType,
                onNavigateBack = { navController.navigate("main_home") },
                onNavigateToCreateTeam = onNavigateToTeamRegistration,
                onNavigateToTeamDetails = { teamId ->
                    navController.navigate("team_details/$teamId")
                }
            )
        }

        // News screen
        composable("main_news") {
            NewsFeedScreen(
                hockeyType = parsedHockeyType,
                onNavigateBack = { navController.navigate("main_home") },
                onNavigateToAddNews = onNavigateToAddNews,
                onNavigateToNewsDetails = { newsId ->
                    navController.navigate("news_details/$newsId")
                }
            )
        }

        // Players screen
        composable("main_players") {
            PlayerManagementScreen(
                hockeyType = parsedHockeyType,
                onNavigateBack = { navController.navigate("main_home") }
            )
        }

        // Profile screen
        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Event details screen
        composable(
            route = "event_details/{hockeyType}/{eventId}",
            arguments = listOf(
                navArgument("hockeyType") { type = NavType.StringType },
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val eventHockeyType = try {
                HockeyType.valueOf(hockeyTypeStr)
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""

            EventDetailsScreen(
                eventId = eventId,
                hockeyType = eventHockeyType,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // News details screen
        composable(
            route = "news_details/{newsId}",
            arguments = listOf(navArgument("newsId") { type = NavType.StringType })
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId") ?: ""
            NewsDetailsScreen(
                newsId = newsId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}