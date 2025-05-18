package com.map711s.namibiahockey.navigation

import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavController,
    startDestination: String = Routes.SPLASH
){
    val showBottomBar = navController.currentBackStackEntryAsState().value?.destination?.route in listOf(
        Routes.HOME,
        Routes.TEAM_REGISTRATION,
        Routes.EVENT_ENTRIES,
        Routes.PLAYER_MANAGEMENT,
        Routes.NEWS_FEED
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { }

}