package com.map711s.namibiahockey.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.screens.admin.RoleChangeRequestsManagementScreen
import com.map711s.namibiahockey.screens.auth.LoginScreen
import com.map711s.namibiahockey.screens.auth.RegisterScreen
import com.map711s.namibiahockey.screens.events.AddEventScreen
import com.map711s.namibiahockey.screens.events.EventDetailsScreen
import com.map711s.namibiahockey.screens.events.EventEntriesScreen
import com.map711s.namibiahockey.screens.hockey.HockeyTypeSelectionScreen
import com.map711s.namibiahockey.screens.main.MainAppScaffold
import com.map711s.namibiahockey.screens.newsfeed.AddNewsScreen
import com.map711s.namibiahockey.screens.newsfeed.NewsDetailsScreen
import com.map711s.namibiahockey.screens.newsfeed.NewsFeedScreen
import com.map711s.namibiahockey.screens.player.PlayerManagementScreen
import com.map711s.namibiahockey.screens.player.PlayerProfileDetailsScreen
import com.map711s.namibiahockey.screens.profile.EditProfileScreen
import com.map711s.namibiahockey.screens.profile.ProfileScreen
import com.map711s.namibiahockey.screens.profile.UserRoleRequestsScreen
import com.map711s.namibiahockey.screens.splash.SplashScreen
import com.map711s.namibiahockey.screens.team.TeamDetailsScreen
import com.map711s.namibiahockey.screens.team.TeamManagementScreen
import com.map711s.namibiahockey.screens.team.TeamRegistrationScreen

@Composable
fun NamibiaHockeyNavHost(
    navController: NavHostController,
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        // Authentication screens
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToHome = {
                    navController.navigate(Routes.HOCKEY_TYPE_SELECTION) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onNavigateToHome = {
                    navController.navigate(Routes.HOCKEY_TYPE_SELECTION) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Hockey type selection screen
        composable(Routes.HOCKEY_TYPE_SELECTION) {
            HockeyTypeSelectionScreen(
                onHockeyTypeSelected = { hockeyType ->
                    navController.navigate(Routes.mainApp(hockeyType.name)) {
                        popUpTo(Routes.HOCKEY_TYPE_SELECTION) { inclusive = true }
                    }
                }
            )
        }

        // Main app with bottom navigation
        composable(
            route = Routes.MAIN_APP,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = try {
                HockeyType.valueOf(hockeyTypeStr)
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }

            MainAppScaffold(
                navController = navController,
                hockeyType = hockeyType,
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToAddEvent = {
                    navController.navigate(Routes.addEvent(hockeyType.name))
                },
                onNavigateToAddNews = {
                    navController.navigate(Routes.addNews(hockeyType.name))
                },
                onNavigateToEventDetails = { eventId, eventHockeyType ->
                    navController.navigate(Routes.eventDetails(eventHockeyType.name, eventId))
                },
                onNavigateToNewsDetails = { newsId ->
                    navController.navigate(Routes.newsDetails(newsId))
                },
                onNavigateToTeamRegistration = {
                    navController.navigate(Routes.teamRegistration(hockeyType.name))
                },
                onNavigateToTeamDetails = { teamId ->
                    navController.navigate(Routes.teamDetails(teamId))
                },
                onSwitchHockeyType = { newHockeyType ->
                    navController.navigate(Routes.mainApp(newHockeyType.name)) {
                        popUpTo(Routes.MAIN_APP) { inclusive = true }
                    }
                }
            )
        }

        // Existing detailed screens
        composable(
            route = Routes.TEAM_REGISTRATION,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = try {
                HockeyType.valueOf(hockeyTypeStr)
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }

            TeamRegistrationScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Routes.EVENT_ENTRIES,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = try {
                HockeyType.valueOf(hockeyTypeStr)
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }

            EventEntriesScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAddEvent = {
                    navController.navigate(Routes.addEvent(hockeyType.name))
                },
                onNavigateToEventDetails = { eventId, eventHockeyType ->
                    navController.navigate(Routes.eventDetails(eventHockeyType.name, eventId))
                }
            )
        }

        composable(
            route = Routes.EVENT_DETAILS,
            arguments = listOf(
                navArgument("hockeyType") { type = NavType.StringType },
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = try {
                HockeyType.valueOf(hockeyTypeStr)
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""

            EventDetailsScreen(
                eventId = eventId,
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Routes.ADD_EVENT,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = try {
                HockeyType.valueOf(hockeyTypeStr)
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }

            AddEventScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEvents = {
                    navController.navigate(Routes.eventEntries(hockeyType.name)) {
                        popUpTo(Routes.EVENT_ENTRIES) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.PLAYER_MANAGEMENT,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = try {
                HockeyType.valueOf(hockeyTypeStr)
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }

            PlayerManagementScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Routes.NEWS_FEED,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = try {
                HockeyType.valueOf(hockeyTypeStr)
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }

            NewsFeedScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAddNews = {
                    navController.navigate(Routes.addNews(hockeyType.name))
                },
                onNavigateToNewsDetails = { newsId ->
                    navController.navigate(Routes.newsDetails(newsId))
                }
            )
        }

        composable(
            route = Routes.NEWS_DETAILS,
            arguments = listOf(navArgument("newsId") { type = NavType.StringType })
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId") ?: ""

            NewsDetailsScreen(
                newsId = newsId,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Routes.ADD_NEWS,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = try {
                HockeyType.valueOf(hockeyTypeStr)
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }

            AddNewsScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToNews = {
                    navController.navigate(Routes.newsFeed(hockeyType.name)) {
                        popUpTo(Routes.NEWS_FEED) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                onNavigateToPlayerManagement = {
                    navController.navigate(Routes.playerManagement("OUTDOOR"))
                },
                onNavigateToTeamManagement = {
                    navController.navigate(Routes.teamManagement("OUTDOOR"))
                },
                onNavigateToRoleChangeRequests = {
                    navController.navigate(Routes.ROLE_CHANGE_REQUESTS)
                }
            )
        }

        // Role Change Requests Management Screen (Admin only)
        composable(Routes.ROLE_CHANGE_REQUESTS) {
            RoleChangeRequestsManagementScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // User Role Requests Screen
        composable(
            route = Routes.USER_ROLE_REQUESTS,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""

            UserRoleRequestsScreen(
                userId = userId,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Edit Profile Screen
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                onNavigateBack = { navController.navigateUp() },
                onSaveSuccess = { navController.navigateUp() }
            )
        }

        // Player Profile Details Screen
        composable(
            route = Routes.PLAYER_PROFILE_DETAILS,
            arguments = listOf(navArgument("playerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""

            PlayerProfileDetailsScreen(
                playerId = playerId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEdit = {
                    // Navigate to player edit screen (to be implemented)
                    navController.navigate("edit_player/$playerId")
                }
            )
        }

        // Player Profile Details Screen
        composable(
            route = Routes.PLAYER_PROFILE_DETAILS,
            arguments = listOf(navArgument("playerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""

            PlayerProfileDetailsScreen(
                playerId = playerId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEdit = {
                    // Navigate to player edit screen (to be implemented)
                    navController.navigate("edit_player/$playerId")
                }
            )
        }

        // Enhanced Player Management Screen
        composable(
            route = Routes.PLAYER_MANAGEMENT,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = try {
                HockeyType.valueOf(hockeyTypeStr)
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }

            PlayerManagementScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToPlayerDetails = { playerId ->
                    navController.navigate(Routes.playerProfileDetails(playerId))
                }
            )
        }

        // Enhanced Team Management Screen
        composable(
            route = Routes.TEAM_MANAGEMENT,
            arguments = listOf(navArgument("hockeyType") { type = NavType.StringType })
        ) { backStackEntry ->
            val hockeyTypeStr = backStackEntry.arguments?.getString("hockeyType") ?: HockeyType.OUTDOOR.name
            val hockeyType = try {
                HockeyType.valueOf(hockeyTypeStr)
            } catch (e: Exception) {
                HockeyType.OUTDOOR
            }

            TeamManagementScreen(
                hockeyType = hockeyType,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToCreateTeam = {
                    navController.navigate(Routes.teamRegistration(hockeyType.name))
                },
                onNavigateToTeamDetails = { teamId ->
                    navController.navigate(Routes.teamDetails(teamId))
                },
                onNavigateToPlayerManagement = { teamId ->
                    navController.navigate(Routes.playerManagement(hockeyType.name))
                }
            )
        }

        composable(
            route = Routes.TEAM_DETAILS,
            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId") ?: ""

            TeamDetailsScreen(
                teamId = teamId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToPlayerManagement = { id ->
                    navController.navigate(Routes.playerManagement("OUTDOOR"))
                }
            )
        }
    }

}