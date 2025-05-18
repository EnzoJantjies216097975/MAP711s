package com.map711s.namibiahockey.util

import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepLinkHandler{

    // Domain constants
    private val WEBSITE_DOMAIN = "namibiahockey.org"
    private val APP_SCHEME = "namibiahockey"

    // Process deep links coming from intents
    fun handleDeepLink(intent: Intent?, navController: NavController) {
        intent?.data?.let { uri ->
            val deepLinkRoute = parseDeepLink(uri)
            if (deepLinkRoute.isNotEmpty()) {
                navController.navigate(deepLinkRoute)
            }
        }
    }

    // Parse URI and convert to navigation route
    private fun parseDeepLink(uri: Uri): String {
        // Web URL format: https://namibiahockey.org/events/123
        // Custom URI format: namibiahockey://events/123

        return when {
            // Process events deep links
            isEventDeepLink(uri) -> {
                val eventId = extractIdFromPath(uri)
                "event_details/$eventId"
            }

            // Process news deep links
            isNewsDeepLink(uri) -> {
                val newsId = extractIdFromPath(uri)
                "news_details/$newsId"
            }

            // Process team deep links
            isTeamDeepLink(uri) -> {
                val teamId = extractIdFromPath(uri)
                "team_details/$teamId"
            }

            // Default to home if no match
            else -> ""
        }
    }

    private fun isEventDeepLink(uri: Uri): Boolean {
        return (uri.host == WEBSITE_DOMAIN && uri.pathSegments.firstOrNull() == "events") ||
                (uri.scheme == APP_SCHEME && uri.host == "events")
    }

    private fun isNewsDeepLink(uri: Uri): Boolean {
        return (uri.host == WEBSITE_DOMAIN && uri.pathSegments.firstOrNull() == "news") ||
                (uri.scheme == APP_SCHEME && uri.host == "news")
    }

    private fun isTeamDeepLink(uri: Uri): Boolean {
        return (uri.host == WEBSITE_DOMAIN && uri.pathSegments.firstOrNull() == "teams") ||
                (uri.scheme == APP_SCHEME && uri.host == "teams")
    }

    private fun extractIdFromPath(uri: Uri): String {
        // Extract ID from path (last segment)
        val segments = uri.pathSegments
        return if (segments.size >= 2) {
            segments.last()
        } else if (segments.size == 1 && uri.getQueryParameter("id") != null) {
            uri.getQueryParameter("id") ?: ""
        } else {
            ""
        }
    }

    // Generate deep link URLs for sharing
    fun generateDeepLinkUrl(type: String, id: String): String {
        return "https://$WEBSITE_DOMAIN/$type/$id"
    }
}