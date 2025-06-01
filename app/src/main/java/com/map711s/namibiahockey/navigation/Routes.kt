package com.map711s.namibiahockey.navigation

// Define all navigation routes here
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val HOCKEY_TYPE_SELECTION = "hockey_type_selection"

    // Main app with bottom navigation
    const val MAIN_APP = "main_app/{hockeyType}"

    // Main screens (accessed via bottom navigation)
    const val MAIN_HOME = "main_home"
    const val MAIN_EVENTS = "main_events"
    const val MAIN_TEAMS = "main_teams"
    const val MAIN_NEWS = "main_news"
    const val MAIN_PLAYERS = "main_players"

    // Home with hockey type
    const val HOME_WITH_TYPE = "home/{hockeyType}"

    // Bottom navigation routes
    const val BOTTOM_HOME = "bottom_home"
    const val BOTTOM_TEAMS = "bottom_teams"
    const val BOTTOM_EVENTS = "bottom_events"
    const val BOTTOM_NEWS = "bottom_news"
    const val BOTTOM_PROFILE = "bottom_profile"
    const val BOTTOM_PLAYERS = "bottom_players"

    // Detail screens
    const val EVENT_DETAILS = "event_details/{eventId}"
    const val NEWS_DETAILS = "news_details/{newsId}"
    const val TEAM_DETAILS = "team_details/{teamId}"

    // Add/Create screens
    const val ADD_EVENT = "add_event/{hockeyType}"
    const val ADD_NEWS = "add_news/{hockeyType}"
    const val TEAM_REGISTRATION = "team_registration/{hockeyType}"

    // Events and other screens with hockey type
    const val EVENT_ENTRIES = "event_entries/{hockeyType}"
    const val PLAYER_MANAGEMENT = "player_management/{hockeyType}"
    const val NEWS_FEED = "news_feed/{hockeyType}"

    const val PROFILE = "profile"

    const val EDIT_PROFILE = "edit_profile"
    const val PLAYER_PROFILE_DETAILS = "player_profile_details/{playerId}"
    const val TEAM_MANAGEMENT = "team_management/{hockeyType}"

    // Role management routes
    const val ROLE_CHANGE_REQUESTS = "role_change_requests"
    const val USER_ROLE_REQUESTS = "user_role_requests/{userId}"

    // Navigation helpers
    fun mainApp(hockeyType: String) = "main_app/$hockeyType"
    fun homeWithType(hockeyType: String) = "home/$hockeyType"
    fun addEvent(hockeyType: String) = "add_event/$hockeyType"
    fun addNews(hockeyType: String) = "add_news/$hockeyType"
    fun teamRegistration(hockeyType: String) = "team_registration/$hockeyType"
    fun eventDetails(eventId: String) = "event_details/$eventId"
    fun newsDetails(newsId: String) = "news_details/$newsId"
    fun teamDetails(teamId: String) = "team_details/$teamId"
    fun eventEntries(hockeyType: String) = "event_entries/$hockeyType"
    fun playerManagement(hockeyType: String) = "player_management/$hockeyType"
    fun newsFeed(hockeyType: String) = "news_feed/$hockeyType"
    fun playerProfileDetails(playerId: String) = "player_profile_details/$playerId"
    fun teamManagement(hockeyType: String) = "team_management/$hockeyType"
    fun userRoleRequests(userId: String) = "user_role_requests/$userId"
}