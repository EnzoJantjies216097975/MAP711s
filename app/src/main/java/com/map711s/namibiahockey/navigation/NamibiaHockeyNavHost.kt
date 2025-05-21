package com.map711s.namibiahockey.navigation

import AddEventScreen
import AddNewsScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.screens.auth.LoginScreen
import com.map711s.namibiahockey.screens.auth.RegisterScreen
import com.map711s.namibiahockey.screens.events.EventEntriesScreen
import com.map711s.namibiahockey.screens.hockey.HockeyTypeSelectionScreen
import com.map711s.namibiahockey.screens.home.HomeScreen
import com.map711s.namibiahockey.screens.newsfeed.NewsFeedScreen
import com.map711s.namibiahockey.screens.player.PlayerManagementScreen
import com.map711s.namibiahockey.screens.profile.ProfileScreen
import com.map711s.namibiahockey.screens.splash.SplashScreen
import com.map711s.namibiahockey.screens.team.TeamRegistrationScreen
import com.map711s.namibiahockey.components.HockeyTypeHeader
import com.map711s.namibiahockey.components.HockeyTypeOptions

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
                    navController.navigate(Routes.HOCKEY_TYPE_SELECTION) {
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
                    navController.navigate(Routes.HOCKEY_TYPE_SELECTION) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Hockey type selection screen
        composable(Routes.HOCKEY_TYPE_SELECTION) {
            HockeyTypeSelectionScreen(
                onHockeyTypeSelected = { hockeyType ->
                    navController.navigate(Routes.homeWithType(hockeyType.name)) {
                        popUpTo(Routes.HOCKEY_TYPE_SELECTION) { inclusive = true }
                    }
                }
            )
        }

        // Main screens with hockey type parameter
        composable(
            route = Routes.HOME_WITH_TYPE,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = HockeyType.valueOf(hockeyTypeStr)

            HomeScreen(
                hockeyType = hockeyType,

                onSwitchHockeyType = { newType ->
                    navController.navigate(Routes.homeWithType(newType.name)) {
                        // Pop only the current Home destination to replace it
                        popUpTo(Routes.HOME_WITH_TYPE) { inclusive = true }
                    }
                },
                onNavigateToTeamRegistration = {
                    navController.navigate(Routes.teamRegistration(hockeyType.name))
                },
                onNavigateToEventEntries = {
                    navController.navigate(Routes.eventEntries(hockeyType.name))
                },
                onNavigateToPlayerManagement = {
                    navController.navigate(Routes.playerManagement(hockeyType.name))
                },
                onNavigateToNewsFeed = {
                    navController.navigate(Routes.newsFeed(hockeyType.name))
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }

        composable(
            route = Routes.TEAM_REGISTRATION,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = HockeyType.valueOf(hockeyTypeStr)

            TeamRegistrationScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Routes.EVENT_ENTRIES,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = HockeyType.valueOf(hockeyTypeStr)

            EventEntriesScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAddEvent = { navController.navigate(Routes.addEvent(hockeyType.name)) }
            )
        }

        composable(
            route = Routes.ADD_EVENT,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = HockeyType.valueOf(hockeyTypeStr)

            AddEventScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEvents = { navController.navigate(Routes.eventEntries(hockeyType.name)) }
            )
        }

        composable(
            route = Routes.PLAYER_MANAGEMENT,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = HockeyType.valueOf(hockeyTypeStr)

            PlayerManagementScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Routes.NEWS_FEED,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = HockeyType.valueOf(hockeyTypeStr)

            NewsFeedScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAddNews = { navController.navigate(Routes.addNews(hockeyType.name)) }
            )
        }

        composable(
            route = Routes.ADD_NEWS,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = HockeyType.valueOf(hockeyTypeStr)

            AddNewsScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToNews = { navController.navigate(Routes.newsFeed(hockeyType.name)) }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}