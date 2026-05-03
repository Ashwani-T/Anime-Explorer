package com.example.animeexplorer.core

import android.app.Application
import com.example.animeexplorer.notifications.NotificationChannelInitializer.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class AppContainer: Application(){
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
    }
}