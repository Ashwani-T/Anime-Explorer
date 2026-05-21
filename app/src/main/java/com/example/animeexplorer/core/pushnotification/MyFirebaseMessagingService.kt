package com.example.animeexplorer.core.pushnotification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.animeexplorer.notifications.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint



class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Here you can send the token to your server if needed
    }

    override fun onMessageReceived(remoteMessage: com.google.firebase.messaging.RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle the received message and show a notification if needed

        remoteMessage.data.also {
                showNotificaton(it["title"] ?: "Anime Reminder", it["message"] ?: "It's time to watch your anime!")
        }
    }

    private fun showNotificaton(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, "anime_reminder_channel")
            .setSmallIcon(com.example.animeexplorer.R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)  as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

    }


}