package com.map711s.namibiahockey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.map711s.namibiahockey.navigation.BottomNavigationBar
import com.map711s.namibiahockey.navigation.NamibiaHockeyNavHost
import com.map711s.namibiahockey.theme.NamibiaHockeyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.map711s.namibiahockey.navigation.Routes

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NamibiaHockeyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NamibiaHockeyApp()
                }
            }
        }
    }
}

@Composable
fun NamibiaHockeyApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // List of routes where bottom navigation should be visible
    val bottomNavRoutes = listOf(
        Routes.HOME,
        Routes.TEAM_REGISTRATION,
        Routes.EVENT_ENTRIES,
        Routes.NEWS_FEED,
        Routes.PROFILE,
        Routes.PLAYER_MANAGEMENT
    )

    // Determine if bottom navigation should be shown
    val showBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NamibiaHockeyNavHost(
            navController = navController,
            modifier = Modifier.padding(
                bottom = if (showBottomNav) innerPadding.calculateBottomPadding() else 0.dp,
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
            )
        )
    }
}