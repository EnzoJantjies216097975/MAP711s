package com.map711s.namibiahockey.navigation

import androidx.navigation.NavHostController

class NamibiaHockeyNavigation(private val navController: NavHostController) {
    val currentRoute: String?
        get() = navController.currentDestination?.route

    fun navigateTo(screen: Screen, popUpTo: Screen? = null, inclusive: Boolean = false) {
        navController.navigate(screen.route) {
            // Avoid building up a large stack of destinations
            popUpTo?.let {
                popUpTo(it.route) {
                    this.inclusive = inclusive
                    saveState = true
                }
            }
            // Avoid multiple copies of the same destination
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    fun navigateUp() {
        navController.navigateUp()
    }

    fun popBackStack() {
        navController.popBackStack()
    }
}