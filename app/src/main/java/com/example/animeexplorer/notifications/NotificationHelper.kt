package com.example.animeexplorer.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.animeexplorer.MainActivity
import com.example.animeexplorer.R
import androidx.core.net.toUri


object NotificationHelper {
    const val ACTION_SNOOZE = "ACTION_SNOOZE"
    const val ACTION_DISMISS = "ACTION_DISMISS"


    fun buildNotification(
        context: Context,
        animeId: String,
        notificationId: Int
    ): Notification {

        val deepLinkUri = "animeexplorer://animedetail?malId=$animeId".toUri()

        val contentIntent = Intent(Intent.ACTION_VIEW, deepLinkUri)
            .apply { setPackage(context.packageName) }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozePendingIntent = createSnoozeIntent(context, animeId)

        val dismissPendingIntent = createDismissIntent(context, notificationId)

        return NotificationCompat.Builder(context, "anime_explorer_channel")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Continue watching?")
            .setContentText("You haven't started this anime yet.")
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

            .addAction(R.drawable.ic_snooze_24, "Snooze", snoozePendingIntent)
            .addAction(R.drawable.outline_remove_24, "Dismiss", dismissPendingIntent)

            .build()
    }

    private fun createSnoozeIntent(
        context: Context,
        animeId: String
    ): PendingIntent {

        val intent = Intent(context, AnimeNotificationReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra("anime_id", animeId)
        }

        return PendingIntent.getBroadcast(
            context,
            animeId.hashCode(), // unique
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createDismissIntent(
        context: Context,
        notificationId: Int
    ): PendingIntent {

        val intent = Intent(context, AnimeNotificationReceiver::class.java).apply {
            action = ACTION_DISMISS
            putExtra("notification_id", notificationId)
        }

        return PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}