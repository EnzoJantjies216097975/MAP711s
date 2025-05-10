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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.map711s.namibiahockey.data.model.BottomNavItem

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    // Define navigation items
    val items = listOf(
        BottomNavItem(
            name = "Teams",
            route = Routes.TEAM_REGISTRATION,
            icon = Icons.Filled.Groups
        ),
        BottomNavItem(
            name = "Events",
            route = Routes.EVENT_ENTRIES,
            icon = Icons.Filled.CalendarMonth
        ),
        BottomNavItem(
            name = "Home",
            route = Routes.HOME,
            icon = Icons.Filled.Home
        ),
        BottomNavItem(
            name = "News",
            route = Routes.NEWS_FEED,
            icon = Icons.Filled.Info
        ),
        BottomNavItem(
            name = "Profile",
            route = Routes.PROFILE,
            icon = Icons.Filled.Person
        )
    )

    // Get current route to highlight the tab
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(text = item.name) },
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
                            // re-selecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}