package com.example.animeexplorer.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.example.animeexplorer.notifications.NotificationChannelInitializer.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class AppContainer : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(
            id = "anime_explorer_channel",
            name = "Anime Explorer Notifications",
            importance = NotificationManager.IMPORTANCE_DEFAULT,
            channelDescription = "Channel for Anime Explorer notifications",
            context = this
        )

        createNotificationChannel(
            id = "anime_reminder_channel",
            name = "Anime Reminder Notifications",
            importance = NotificationManager.IMPORTANCE_DEFAULT,
            channelDescription = "Channel for Anime Reminder notifications",
            context = this
        )
    }
}