package com.map711s.namibiahockey.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.map711s.namibiahockey.ui.auth.ForgotPasswordScreen
import com.map711s.namibiahockey.ui.auth.LoginScreen
import com.map711s.namibiahockey.ui.auth.RegisterScreen
import com.map711s.namibiahockey.ui.dashboard.DashboardScreen
import com.map711s.namibiahockey.ui.events.EventDetailScreen
import com.map711s.namibiahockey.ui.events.EventRegistrationScreen
import com.map711s.namibiahockey.ui.events.EventsScreen
import com.map711s.namibiahockey.ui.players.PlayerDetailScreen
import com.map711s.namibiahockey.ui.players.PlayerListScreen
import com.map711s.namibiahockey.ui.settings.SettingsScreen
import com.map711s.namibiahockey.ui.teams.TeamListScreen
import com.map711s.namibiahockey.ui.teams.TeamRegistrationScreen
import com.map711s.namibiahockey.util.Constants
import com.map711s.namibiahockey.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
){
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Constants.Routes.DASHBOARD else Constants.Routes.LOGIN,
        modifier = modifier
    ){
        // Auth screens
        composable(Constants.Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Constants.Routes.DASHBOARD) {
                    popUpTo(Constants.Routes.LOGIN) { inclusive = true }
                }},
                onNavigateToRegister = { navController.navigate(Constants.Routes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(Constants.Routes.FORGOT_PASSWORD) }
            )
        }

        composable(Constants.Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Constants.Routes.DASHBOARD) {
                    popUpTo(Constants.Routes.LOGIN) { inclusive = true }
                }},
                onNavigateToLogin = { navController.navigate(Constants.Routes.LOGIN) {
                    popUpTo(Constants.Routes.REGISTER) { inclusive = true }
                }}
            )
        }

        composable(Constants.Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackToLogin = { navController.navigate(Constants.Routes.LOGIN) {
                    popUpTo(Constants.Routes.FORGOT_PASSWORD) { inclusive = true }
                }}
            )
        }

        // Main screens
        composable(Constants.Routes.DASHBOARD) {
            DashboardScreen()
        }

        composable(Constants.Routes.TEAMS) {
            TeamListScreen(
                onTeamClick = { teamId ->
                    navController.navigate(Constants.Routes.teamDetail(teamId))
                },
                onAddTeamClick = {
                    navController.navigate(Constants.Routes.TEAM_REGISTRATION)
                }
            )
        }

        composable(Constants.Routes.EVENTS) {
            EventsScreen(
                onEventClick = { eventId ->
                    navController.navigate(Constants.Routes.eventDetail(eventId))
                },
                onAddEventClick = {
                    // Implement if needed
                }
            )
        }

        composable(Constants.Routes.PLAYERS) {
            PlayerListScreen(
                onPlayerClick = { playerId ->
                    navController.navigate(Constants.Routes.playerDetail(playerId))
                },
                onAddPlayerClick = {
                    navController.navigate(Constants.Routes.PLAYER_REGISTRATION)
                }
            )
        }

        composable(Constants.Routes.SETTINGS) {
            SettingsScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Constants.Routes.LOGIN) {
                        popUpTo(Constants.Routes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        // Detail screens
        composable(
            Constants.Routes.TEAM_DETAIL,
            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId") ?: ""
            // Implement TeamDetailScreen
        }

        composable(
            Constants.Routes.PLAYER_DETAIL,
            arguments = listOf(navArgument("playerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""
            PlayerDetailScreen(
                playerId = playerId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { playerId ->
                    navController.navigate(Constants.Routes.playerEdit(playerId))
                }
            )
        }

        composable(
            Constants.Routes.EVENT_DETAIL,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { eventId ->
                    navController.navigate(Constants.Routes.eventRegistration(eventId))
                }
            )
        }

        // Registration screens
        composable(Constants.Routes.TEAM_REGISTRATION) {
            TeamRegistrationScreen(
                onNavigateBack = { navController.popBackStack() },
                onTeamRegistered = { navController.popBackStack() }
            )
        }

        composable(
            Constants.Routes.EVENT_REGISTRATION,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventRegistrationScreen(
                eventId = eventId,
                onBackClick = { navController.popBackStack() },
                onRegistrationComplete = { navController.popBackStack() }
            )
        }
    }
}