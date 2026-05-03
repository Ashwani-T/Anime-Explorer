package com.example.animeexplorer.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannelInitializer {
    fun createNotificationChannel(context: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                "anime_explorer_channel",
                 "Anime Explorer Notifications",
                 NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Anime Explorer notifications"
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}