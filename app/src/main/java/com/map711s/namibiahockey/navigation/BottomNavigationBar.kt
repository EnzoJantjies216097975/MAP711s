package com.map711s.namibiahockey.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navItems = listOf(
        BottomNavItem(
            name = "Home",
            route = Routes.HOME,
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            name = "Teams",
            route = Routes.TEAM_REGISTRATION,
            icon = Icons.Default.Groups
        ),
        BottomNavItem(
            name = "Events",
            route = Routes.EVENT_ENTRIES,
            icon = Icons.Default.CalendarMonth
        ),
        BottomNavItem(
            name = "Players",
            route = Routes.PLAYER_MANAGEMENT,
            icon = Icons.Default.Person
        ),
        BottomNavItem(
            name = "News",
            route = Routes.NEWS_FEED,
            icon = Icons.Default.Info
        )
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(Routes.HOME) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(text = item.name) }
            )
        }
    }
}