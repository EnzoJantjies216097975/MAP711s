package com.map711s.namibiahockey

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.map711s.namibiahockey.navigation.BottomNavigationBar
import com.map711s.namibiahockey.navigation.NamibiaHockeyNavHost
import com.map711s.namibiahockey.ui.theme.NamibiaHockeyTheme
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.map711s.namibiahockey.navigation.Screen
import com.map711s.namibiahockey.presentation.common.OfflineStatusBar
import com.map711s.namibiahockey.ui.theme.NHUSpacing
import com.map711s.namibiahockey.ui.components.dialogs.NotificationPermissionHandler
import com.map711s.namibiahockey.util.DeepLinkHandler
import com.map711s.namibiahockey.util.NetworkMonitor
import com.map711s.namibiahockey.util.NotificationManager
import com.map711s.namibiahockey.util.WindowSize
import com.map711s.namibiahockey.util.WindowSizeClass
import com.map711s.namibiahockey.util.rememberContentPadding
import com.map711s.namibiahockey.util.rememberWindowSize
import javax.inject.Inject
import android.util.Log
import androidx.navigation.NavHostController

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // Note: Changed to NavController type to match what rememberNavController returns
    private lateinit var navController: NavController

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var deepLinkHandler: DeepLinkHandler

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {  // Fixed: Added the savedInstanceState parameter
        super.onCreate(savedInstanceState)

        setContent {
            // Get window size information
            val windowSize = rememberWindowSize()
            val contentPadding = rememberContentPadding(windowSize)
            val navControllerInstance = rememberNavController()

            // Store the navController instance
            navController = navControllerInstance

            // Handle deep links
            LaunchedEffect(intent) {
                try {
                    deepLinkHandler.handleDeepLink(intent, navControllerInstance)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Deep link error: ${e.message}", e)
                }
            }

            // Check for notification permission
            NotificationPermissionHandler(
                notificationManager = notificationManager
            )

            NamibiaHockeyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        OfflineStatusBar(networkMonitor = networkMonitor)
                        NamibiaHockeyApp(
                            windowSize = windowSize,
                            contentPadding = contentPadding,
                            navController = navControllerInstance  // Pass the NavController instance
                        )
                    }
                }
            }
        }
    }

    // Handle new intents (e.g., when app is already running)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        try {
            setIntent(intent)
            if (::navController.isInitialized) {
                // Using the stored navController instance
                deepLinkHandler.handleDeepLink(intent, navController)
            } else {
                Log.e("MainActivity", "NavController not initialized in onNewIntent")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onNewIntent: ${e.message}", e)
        }
    }
}

@Composable
fun NamibiaHockeyApp(
    windowSize: WindowSize,
    contentPadding: Dp,
    navController: NavHostController  // Changed parameter type to NavController
) {
    // Don't create a new NavController, use the one passed as parameter
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
                navController = navController,  // Pass the NavController
                showBottomNav = showBottomNav,
                contentPadding = contentPadding
            )
        }
        WindowSizeClass.MEDIUM, WindowSizeClass.EXPANDED -> {
            // Tablet/Desktop layout
            ExpandedLayout(
                navController = navController,  // Pass the NavController
                showBottomNav = showBottomNav,
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
fun CompactLayout(
    navController: NavHostController,  // Changed parameter type to NavController
    showBottomNav: Boolean,
    contentPadding: Dp
) {
    // Implementation remains the same, just adapted for NavController
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
    navController: NavHostController,  // Changed parameter type to NavController
    showBottomNav: Boolean,
    contentPadding: Dp
) {
    // Implementation remains the same, just adapted for NavController
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
fun NavigationRailItems(navController: NavHostController) {  // Changed parameter type to NavController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Rest of the implementation remains the same
    // ...
}

data class NavigationItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)