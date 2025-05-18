package com.map711s.namibiahockey.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.map711s.namibiahockey.di.ServiceLocator
import com.map711s.namibiahockey.di.ServiceLocator.notificationManager
import com.map711s.namibiahockey.util.NotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HockeyMessagingService : FirebaseMessagingService() {

//    @Inject
//    lateinit var notificationManager: NotificationManager

    private val notificationManager by lazy { ServiceLocator.notificationManager }
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Extract notification data
        val title = remoteMessage.notification?.title ?: "Namibia Hockey"
        val body = remoteMessage.notification?.body ?: ""

        // Get data payload
        val data = remoteMessage.data
        val notificationType = data["type"] ?: "general"
        val deepLink = data["deepLink"]

        // Determine channel based on notification type
        val channelId = when (notificationType) {
            "event" -> NotificationManager.CHANNEL_EVENTS
            "news" -> NotificationManager.CHANNEL_NEWS
            "team" -> NotificationManager.CHANNEL_TEAMS
            else -> NotificationManager.CHANNEL_GENERAL
        }

        // Show notification
        notificationManager.showNotification(
            title = title,
            message = body,
            channelId = channelId,
            deepLinkUri = deepLink
        )
    }

    override fun onNewToken(token: String) {
        // Handle new FCM token
        Log.d("FCM", "New token: $token")

        // Send token to your server
        serviceScope.launch {
            // Store on server
        }
    }
}