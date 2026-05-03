package com.example.animeexplorer.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class AnimeNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "ACTION_SNOOZE" -> {
                val animeId = intent.getIntExtra("anime_id",1)

                //AnimeReminderScheduler.scheduleReminder(context, animeId, 15 * 60 * 1000L) // Snooze for 15 minutes
            }

            "ACTION_DISMISS" -> {
                val notificationId = intent.getIntExtra("notification_id", 0)
                NotificationManagerCompat.from(context)
                    .cancel(notificationId)
            }
        }
    }
}