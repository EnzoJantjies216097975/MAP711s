package com.map711s.namibiahockey.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.map711s.namibiahockey.MainActivity
import com.map711s.namibiahockey.R
import com.map711s.namibiahockey.data.local.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HockeyMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send the token to your server for targeting specific users
        // You could store this in Firestore under the user's document
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Check if notifications are enabled
        if (!preferencesManager.notificationsEnabled.value) {
            return
        }

        val title = message.data["title"] ?: "Namibia Hockey"
        val body = message.data["body"] ?: ""
        val type = message.data["type"] ?: "general"
        val id = message.data["id"] ?: ""

        createNotificationChannel()

        val intent = when(type) {
            "event" -> Intent(this, MainActivity::class.java).apply {
                putExtra("DESTINATION", "event_detail")
                putExtra("EVENT_ID", id)
            }
            "team" -> Intent(this, MainActivity::class.java).apply {
                putExtra("DESTINATION", "team_detail")
                putExtra("TEAM_ID", id)
            }
            "match" -> Intent(this, MainActivity::class.java).apply {
                putExtra("DESTINATION", "match_detail")
                putExtra("MATCH_ID", id)
            }
            else -> Intent(this, MainActivity::class.java)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Namibia Hockey Updates"
            val descriptionText = "Notifications for match updates, events, and team changes"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "namibia_hockey_channel"
    }
}