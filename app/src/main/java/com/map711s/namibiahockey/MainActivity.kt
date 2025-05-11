package com.map711s.namibiahockey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.map711s.namibiahockey.navigation.Screen
import com.map711s.namibiahockey.presentation.common.OfflineStatusBar
import com.map711s.namibiahockey.theme.NHUSpacing
import com.map711s.namibiahockey.util.NetworkMonitor
import com.map711s.namibiahockey.util.WindowSize
import com.map711s.namibiahockey.util.WindowSizeClass
import com.map711s.namibiahockey.util.rememberContentPadding
import com.map711s.namibiahockey.util.rememberWindowSize
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Get window size information
            val windowSize = rememberWindowSize()
            val contentPadding = rememberContentPadding(windowSize)

            NamibiaHockeyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        OfflineStatusBar(networkMonitor = networkMonitor)
                        NamibiaHockeyApp(windowSize = windowSize, contentPadding = contentPadding)
                    }
                }
            }
        }
    }
}

@Composable
fun NamibiaHockeyApp(
    windowSize: WindowSize,
    contentPadding: Dp
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // List of routes where bottom navigation should be visible
    val bottomNavRoutes = listOf(
        Screen.Home.route,
        Screen.TeamRegistration.route,
        Screen.EventEntries.route,
        Screen.NewsFeed.route,
        Screen.Profile.route,
        Screen.PlayerManagement.route
    )

    // Determine if bottom navigation should be shown
    val showBottomNav = currentRoute in bottomNavRoutes

    // Use different layouts based on window size
    when (windowSize.width) {
        WindowSizeClass.COMPACT -> {
            // Phone layout
            CompactLayout(
                navController = navController,
                showBottomNav = showBottomNav,
                contentPadding = contentPadding
            )
        }
        WindowSizeClass.MEDIUM, WindowSizeClass.EXPANDED -> {
            // Tablet/Desktop layout
            ExpandedLayout(
                navController = navController,
                showBottomNav = showBottomNav,
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
fun CompactLayout(
    navController: NavHostController,
    showBottomNav: Boolean,
    contentPadding: Dp
) {
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
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + contentPadding,
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + contentPadding
            )
        )
    }
}

@Composable
fun ExpandedLayout(
    navController: NavHostController,
    showBottomNav: Boolean,
    contentPadding: Dp
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left navigation rail (instead of bottom nav)
        if (showBottomNav) {
            NavigationRail(
                modifier = Modifier.fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(NHUSpacing.md, Alignment.CenterVertically)
                ) {
                    NavigationRailItems(navController)
                }
            }
        }

        // Main content area with extra padding for tablet/desktop
        NamibiaHockeyNavHost(
            navController = navController,
            modifier = Modifier
                .weight(1f)
                .padding(
                    horizontal = contentPadding,
                    vertical = NHUSpacing.md
                )
        )
    }
}

@Composable
fun NavigationRailItems(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define navigation items
    val items = listOf(
        NavigationItem(
            name = "Teams",
            route = Screen.TeamRegistration.route,
            icon = Icons.Filled.Groups
        ),
        NavigationItem(
            name = "Events",
            route = Screen.EventEntries.route,
            icon = Icons.Filled.CalendarMonth
        ),
        NavigationItem(
            name = "Home",
            route = Screen.Home.route,
            icon = Icons.Filled.Home
        ),
        NavigationItem(
            name = "News",
            route = Screen.NewsFeed.route,
            icon = Icons.Filled.Info
        ),
        NavigationItem(
            name = "Profile",
            route = Screen.Profile.route,
            icon = Icons.Filled.Person
        )
    )

    items.forEach { item ->
        NavigationRailItem(
            selected = currentRoute == item.route,
            onClick = {
                if (currentRoute != item.route) {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(item.icon, contentDescription = item.name) },
            label = { Text(item.name) }
        )
    }
}

data class NavigationItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)