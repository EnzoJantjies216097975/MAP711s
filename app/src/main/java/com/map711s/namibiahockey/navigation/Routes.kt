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

    // Detail screens
    const val EVENT_DETAILS = "event_details/{hockeyType}/{eventId}"
    const val NEWS_DETAILS = "news_details/{newsId}"
    const val TEAM_DETAILS = "team_details/{teamId}"

    // Add/Create screens
    const val ADD_EVENT = "add_event/{hockeyType}"
    const val ADD_NEWS = "add_news/{hockeyType}"
    const val TEAM_REGISTRATION = "team_registration/{hockeyType}"

    const val PROFILE = "profile"

    // Navigation helpers
    fun mainApp(hockeyType: String) = "main_app/$hockeyType"
    fun addEvent(hockeyType: String) = "add_event/$hockeyType"
    fun addNews(hockeyType: String) = "add_news/$hockeyType"
    fun teamRegistration(hockeyType: String) = "team_registration/$hockeyType"
    fun eventDetails(hockeyType: String, eventId: String) = "event_details/$hockeyType/$eventId"
    fun newsDetails(newsId: String) = "news_details/$newsId"
    fun teamDetails(teamId: String) = "team_details/$teamId"
}