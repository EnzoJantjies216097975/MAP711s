package com.map711s.namibiahockey.navigation

// Define all navigation routes here
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val HOCKEY_TYPE_SELECTION = "hockey_type_selection"

    const val HOME = "home"
    const val HOME_WITH_TYPE = "home/{hockeyType}"

    const val TEAM_REGISTRATION = "team_registration/{hockeyType}"

    const val EVENT_ENTRIES = "event_entries/{hockeyType}"
    const val EVENT_DETAILS = "event_details/{hockeyType}/{eventId}"
    const val ADD_EVENT = "add_event/{hockeyType}"

    const val PLAYER_MANAGEMENT = "player_management/{hockeyType}"

    const val NEWS_FEED = "news_feed/{hockeyType}"
    const val NEWS_DETAILS = "news_details/{newsId}"
    const val ADD_NEWS = "add_news/{hockeyType}"



    const val PROFILE = "profile"


    // Navigation helpers
    fun homeWithType(hockeyType: String) = "home/$hockeyType"
    fun teamRegistration(hockeyType: String) = "team_registration/$hockeyType"
    fun eventEntries(hockeyType: String) = "event_entries/$hockeyType"
    fun playerManagement(hockeyType: String) = "player_management/$hockeyType"
    fun newsFeed(hockeyType: String) = "news_feed/$hockeyType"
    fun addEvent(hockeyType: String) = "add_event/$hockeyType"
    fun addNews(hockeyType: String) = "add_news/$hockeyType"
    fun eventDetails(hockeyType: String, eventId: String) = "event_details/$hockeyType/$eventId"
    fun newsDetails(newsId: String) = "news_details/$newsId"
}