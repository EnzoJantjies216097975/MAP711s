package com.map711s.namibiahockey.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.map711s.namibiahockey.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
}