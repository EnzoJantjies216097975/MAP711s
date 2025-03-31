package com.map711s.namibiahockey.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.map711s.namibiahockey.ui.auth.ForgotPasswordScreen
import com.map711s.namibiahockey.ui.auth.LoginScreen
import com.map711s.namibiahockey.ui.auth.RegisterScreen
import com.map711s.namibiahockey.ui.dashboard.DashboardScreen
import com.map711s.namibiahockey.ui.events.EventDetailScreen
import com.map711s.namibiahockey.ui.events.EventRegistrationScreen
import com.map711s.namibiahockey.ui.events.EventsScreen
import com.map711s.namibiahockey.ui.players.PlayerDetailScreen
import com.map711s.namibiahockey.ui.players.PlayerRegistrationScreen
import com.map711s.namibiahockey.ui.players.PlayerListScreen
import com.map711s.namibiahockey.ui.settings.SettingsScreen
import com.map711s.namibiahockey.ui.teams.TeamDetailScreen
import com.map711s.namibiahockey.ui.teams.TeamListScreen
import com.map711s.namibiahockey.ui.teams.TeamRegistrationScreen
import com.map711s.namibiahockey.util.Constants.Routes
import com.map711s.namibiahockey.util.Resource
import com.map711s.namibiahockey.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = false)

    NavHost(
        navController = navController as NavHostController,
        startDestination = if (isLoggedIn) Routes.DASHBOARD else Routes.LOGIN,
        modifier = modifier
    ) {
        // Auth screens
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                    }
                }
            )
        }

        // Main screens
        composable(Routes.DASHBOARD) {
            DashboardScreen()
        }

        composable(Routes.TEAMS) {
            TeamListScreen(
                onTeamClick = { teamId ->
                    navController.navigate(Routes.teamDetail(teamId))
                },
                onAddTeamClick = {
                    navController.navigate(Routes.TEAM_REGISTRATION)
                }
            )
        }

        composable(Routes.EVENTS) {
            EventsScreen(
                onEventClick = { eventId ->
                    navController.navigate(Routes.eventDetail(eventId))
                },
                onAddEventClick = {
                    // Implement if needed
                }
            )
        }

        composable(Routes.PLAYERS) {
            PlayerListScreen(
                onPlayerClick = { playerId ->
                    navController.navigate(Routes.playerDetail(playerId))
                },
                onAddPlayerClick = {
                    navController.navigate(Routes.PLAYER_REGISTRATION)
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        // Detail screens
        composable(
            Routes.TEAM_DETAIL,
            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId") ?: ""
            TeamDetailScreen(
                teamId = teamId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { teamId ->
                    navController.navigate(Routes.teamEdit(teamId))
                }
            )
        }

        composable(
            Routes.PLAYER_DETAIL,
            arguments = listOf(navArgument("playerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""
            PlayerDetailScreen(
                playerId = playerId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { playerId ->
                    navController.navigate(Routes.playerEdit(playerId))
                }
            )
        }

        composable(
            Routes.EVENT_DETAIL,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { eventId ->
                    navController.navigate(Routes.eventRegistration(eventId))
                }
            )
        }

        // Registration screens
        composable(Routes.TEAM_REGISTRATION) {
            TeamRegistrationScreen(
                onNavigateBack = { navController.popBackStack() },
                onTeamRegistered = { navController.popBackStack() }
            )
        }

        composable(Routes.PLAYER_REGISTRATION) {
            PlayerRegistrationScreen(
                onNavigateBack = { navController.popBackStack() },
                onPlayerRegistered = { navController.popBackStack() }
            )
        }

        composable(
            Routes.EVENT_REGISTRATION,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventRegistrationScreen(
                eventId = eventId,
                onBackClick = { navController.popBackStack() },
                onRegistrationComplete = { navController.popBackStack() }
            )
        }

        // Edit screens
        composable(
            Routes.TEAM_EDIT,
            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId") ?: ""
            // Implement TeamEditScreen or reuse TeamRegistrationScreen with teamId
            TeamRegistrationScreen(
                onNavigateBack = { navController.popBackStack() },
                onTeamRegistered = { navController.popBackStack() },
                teamId = teamId
            )
        }

        composable(
            Routes.PLAYER_EDIT,
            arguments = listOf(navArgument("playerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""
            // Implement PlayerEditScreen or reuse PlayerRegistrationScreen with playerId
            PlayerRegistrationScreen(
                onNavigateBack = { navController.popBackStack() },
                onPlayerRegistered = { navController.popBackStack() },
                playerId = playerId
            )
        }
    }
}