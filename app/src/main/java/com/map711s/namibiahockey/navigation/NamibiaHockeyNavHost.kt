package com.map711s.namibiahockey.navigation

import AddEventScreen
import AddNewsScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
    startDestination: Screen = Screen.Splash,
    modifier: Modifier = Modifier
) {
    val navigation = remember { NamibiaHockeyNavigation(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        // Splash screen
        composable(
            route = Screen.Splash.route,
            enterTransition = { Screen.Splash.enterTransition },
            exitTransition = { Screen.Splash.exitTransition }
        ) {
            SplashScreen(
                onNavigateToLogin = { navigation.navigateTo(Screen.Login, Screen.Splash, true) }
            )
        }

        // Authentication screens
        composable(
            route = Screen.Login.route,
            enterTransition = { Screen.Login.enterTransition },
            exitTransition = { Screen.Login.exitTransition }
        ) {
            LoginScreen(
                onNavigateToRegister = { navigation.navigateTo(Screen.Register) },
                onNavigateToHome = { navigation.navigateTo(Screen.Home, Screen.Login, true) }
            )
        }

        composable(
            route = Screen.Register.route,
            enterTransition = { Screen.Register.enterTransition },
            exitTransition = { Screen.Register.exitTransition}
        ) {
            RegisterScreen(
                onNavigateToLogin = { navigation.navigateTo(Screen.Login, Screen.Home, true) },
                onNavigateToHome = { navigation.navigateTo(Screen.Home, Screen.Login, true)
                }
            )
        }

        // Main screens
        composable(Screen.HOME) {
            HomeScreen(
                onNavigateToTeamRegistration = { navigation.navigateUp(Screen.TeamRegistration) },
                onNavigateToEventEntries = { navigation.navigateTo(Screen.EventEntries)},
                onNavigateToPlayerManagement = { navigation.navigateTo(Screen.PlayerManagement)},
                onNavigateToNewsFeed = {navigation.navigateTo(Screen.NewsFeed) },
                onNavigateToProfile = { navigation.navigateTo(Screen.Profile) }
            )
        }

        composable(Screen.TEAM_REGISTRATION) {
            TeamRegistrationScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToHome = {
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.EVENT_ENTRIES) {
            EventEntriesScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAddEvent = {navController.navigate(Screen.ADD_EVENT)}
            )
        }
        composable(Screen.ADD_EVENT){
            AddEventScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEvents = {navController.navigate(Screen.EVENT_ENTRIES)}
            )
        }

        composable(Screen.PLAYER_MANAGEMENT) {
            PlayerManagementScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.NEWS_FEED) {
            NewsFeedScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAddNews = {navController.navigate(Screen.ADD_NEWS)}
            )
        }
        composable(Screen.ADD_NEWS){
            AddNewsScreen(
                onNavigateBack = {navController.navigateUp()},
                onNavigateToNews = {navController.navigate(Screen.NEWS_FEED)}
            )
        }

        composable(Screen.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.EventDetails.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType }),
            enterTransition = { Screen.EventDetails.enterTransition },
            exitTransition = { Screen.EventDetails.exitTransition }
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailsScreen(
                eventId = eventId,
                onNavigateBack = { navigation.navigateUp(Screen.TeamRegistration) }
            )
        }
    }
}

@Composable
fun EventDetailsScreen(
    eventId: String,
    onNavigateBack: () -> Unit
) {
    Surface {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Event Details for ID: $eventId")
            Button(onClick = onNavigateBack) {
                Text("Back")
            }
        }
    }
}