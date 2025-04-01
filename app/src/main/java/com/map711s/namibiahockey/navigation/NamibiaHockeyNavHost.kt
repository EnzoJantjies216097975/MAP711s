package com.map711s.namibiahockey.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.map711s.namibiahockey.screens.auth.LoginScreen
import com.map711s.namibiahockey.screens.auth.RegisterScreen
import com.map711s.namibiahockey.screens.events.EventEntriesScreen
import com.map711s.namibiahockey.screens.home.HomeScreen
import com.map711s.namibiahockey.screens.newsfeed.NewsFeedScreen
import com.map711s.namibiahockey.screens.player.PlayerManagementScreen
import com.map711s.namibiahockey.screens.profile.ProfileScreen
import com.map711s.namibiahockey.screens.splash.SplashScreen
import com.map711s.namibiahockey.screens.team.TeamRegistrationScreen

@Composable
fun NamibiaHockeyNavHost(
    navController: NavHostController,
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        // Authentication screens
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        // Clear the back stack so user can't go back to login
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Main screens
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToTeamRegistration = { navController.navigate(Routes.TEAM_REGISTRATION) },
                onNavigateToEventEntries = { navController.navigate(Routes.EVENT_ENTRIES) },
                onNavigateToPlayerManagement = { navController.navigate(Routes.PLAYER_MANAGEMENT) },
                onNavigateToNewsFeed = { navController.navigate(Routes.NEWS_FEED) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.TEAM_REGISTRATION) {
            TeamRegistrationScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.EVENT_ENTRIES) {
            EventEntriesScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.PLAYER_MANAGEMENT) {
            PlayerManagementScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.NEWS_FEED) {
            NewsFeedScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}