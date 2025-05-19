package com.map711s.namibiahockey.navigation

import AddEventScreen
import AddNewsScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Define bottom navigation items
        val bottomNavItems = listOf(
            BottomNavItem("Home", Routes.HOME, Icons.Default.Home),
            BottomNavItem("Teams", Routes.TEAM_REGISTRATION, Icons.Default.Groups),
            BottomNavItem("Events", Routes.EVENT_ENTRIES, Icons.Default.CalendarMonth),
            BottomNavItem("Players", Routes.PLAYER_MANAGEMENT, Icons.Default.Person),
            BottomNavItem("News", Routes.NEWS_FEED, Icons.Default.Info)
        )

        // Check if current screen should show bottom navigation
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val showBottomBar = when (currentRoute) {
            Routes.SPLASH, Routes.LOGIN, Routes.REGISTER -> false
            else -> true
        }

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(navController, bottomNavItems)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                // Existing navigation destinations...
                // (rest of your navigation composables remain the same)

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
                        onNavigateBack = { navController.navigateUp() },
                        onNavigateToAddEvent = {navController.navigate(Routes.ADD_EVENT)}
                    )
                }

                composable(Routes.ADD_EVENT){
                    AddEventScreen(
                        onNavigateBack = { navController.navigateUp() },
                        onNavigateToEvents = {navController.navigate(Routes.EVENT_ENTRIES)}
                    )
                }

                composable(Routes.PLAYER_MANAGEMENT) {
                    PlayerManagementScreen(
                        onNavigateBack = { navController.navigateUp() }
                    )
                }

                composable(Routes.NEWS_FEED) {
                    NewsFeedScreen(
                        onNavigateBack = { navController.navigateUp() },
                        onNavigateToAddNews = {navController.navigate(Routes.ADD_NEWS)}
                    )
                }

                composable(Routes.ADD_NEWS){
                    AddNewsScreen(
                        onNavigateBack = {navController.navigateUp()},
                        onNavigateToNews = {navController.navigate(Routes.NEWS_FEED)}
                    )
                }

                composable(Routes.PROFILE) {
                    ProfileScreen(
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }
        }
    }