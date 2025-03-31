package com.map711s.namibiahockey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.map711s.namibiahockey.ui.auth.LoginScreen
import com.map711s.namibiahockey.ui.auth.RegisterScreen
import com.map711s.namibiahockey.ui.auth.ForgotPasswordScreen
import com.map711s.namibiahockey.ui.dashboard.DashboardScreen
import com.map711s.namibiahockey.ui.events.EventsScreen
import com.map711s.namibiahockey.ui.players.PlayerListScreen
import com.map711s.namibiahockey.ui.settings.SettingsScreen
import com.map711s.namibiahockey.ui.teams.TeamListScreen
import com.map711s.namibiahockey.ui.theme.NamibiaHockeyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NamibiaHockeyApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamibiaHockeyApp() {
    NamibiaHockeyTheme {
        val navController = rememberNavController()
        val isLoggedIn = remember { mutableStateOf(false) }

        Surface(color = MaterialTheme.colorScheme.background) {
            if (isLoggedIn.value) {
                MainScreenWithBottomNav(navController)
            } else {
                AuthNavigation(navController, isLoggedIn)
            }
        }
    }
}

@Composable
fun AuthNavigation(
    navController: NavHostController,
    isLoggedIn: MutableState<Boolean>
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { isLoggedIn.value = true },
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgotPassword") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("forgotPassword") {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithBottomNav(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("dashboard", "Dashboard", Icons.Filled.Home),
        BottomNavItem("teams", "Teams", Icons.Outlined.Group),
        BottomNavItem("events", "Events", Icons.Outlined.CalendarMonth),
        BottomNavItem("players", "Players", Icons.Filled.Person),
        BottomNavItem("settings", "Settings", Icons.Filled.Settings)
    )

    var selectedItemIndex by remember { mutableStateOf(0) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    selectedItemIndex = items.indexOfFirst { it.route == currentRoute }.takeIf { it > -1 } ?: 0

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") { DashboardScreen() }
            composable("teams") { TeamListScreen() }
            composable("events") { EventsScreen() }
            composable("players") { PlayerListScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)