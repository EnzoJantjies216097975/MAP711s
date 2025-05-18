package com.map711s.namibiahockey.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.map711s.namibiahockey.MainActivity
import com.map711s.namibiahockey.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Notification channels
        const val CHANNEL_EVENTS = "events_channel"
        const val CHANNEL_NEWS = "news_channel"
        const val CHANNEL_TEAMS = "teams_channel"
        const val CHANNEL_GENERAL = "general_channel"

        // Topic subscriptions
        const val TOPIC_EVENTS = "events"
        const val TOPIC_NEWS = "news"
        const val TOPIC_TEAMS = "teams"
    }

    init {
        createNotificationChannels()
    }

    /**
     * Create notification channels for Android O and above
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_EVENTS,
                    "Events",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Event updates, reminders, and registrations"
                },

                NotificationChannel(
                    CHANNEL_NEWS,
                    "News",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "News updates from the Namibia Hockey Union"
                },

                NotificationChannel(
                    CHANNEL_TEAMS,
                    "Teams",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Team updates and announcements"
                },

                NotificationChannel(
                    CHANNEL_GENERAL,
                    "General",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "General notifications from the app"
                }
            )

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    /**
     * Subscribe to notification topics
     */
    suspend fun subscribeToTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_EVENTS).await()
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NEWS).await()
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_TEAMS).await()
    }

    /**
     * Unsubscribe from notification topics
     */
    suspend fun unsubscribeFromTopics() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_EVENTS).await()
        FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_NEWS).await()
        FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_TEAMS).await()
    }

    /**
     * Get FCM token for the device
     */
    suspend fun getFCMToken(): String {
        return FirebaseMessaging.getInstance().token.await()
    }

    /**
     * Check if notification permissions are granted
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required for Android < 13
        }
    }

    /**
     * Show a notification
     */
    fun showNotification(
        title: String,
        message: String,
        channelId: String = CHANNEL_GENERAL,
        notificationId: Int = System.currentTimeMillis().toInt(),
        deepLinkUri: String? = null
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            deepLinkUri?.let {
                action = Intent.ACTION_VIEW
                data = android.net.Uri.parse(it)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationId, builder.build())
            }
        }
    }
}