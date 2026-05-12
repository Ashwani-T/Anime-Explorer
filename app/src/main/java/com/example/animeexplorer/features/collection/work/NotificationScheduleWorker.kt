package com.example.animeexplorer.features.collection.work

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.animeexplorer.notifications.NotificationHelper
import java.util.concurrent.TimeUnit

class NotificationScheduleWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext = appContext, params = workerParams) {


    override suspend fun doWork(): Result {
        val animeId = inputData.getString(KEY_ANIME_ID)?:"1"
        val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, 0)

        val buildAnimeDetailNotification = NotificationHelper.buildNotification(
            context = applicationContext,
            animeId = animeId,
            notificationId = notificationId
        )
        if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            NotificationManagerCompat.from(applicationContext)
                .notify(notificationId, buildAnimeDetailNotification)
            return Result.success()
        }
        return Result.failure(workDataOf("Permission Error" to "Notification Permission Not Granted"))
    }

    companion object {
        const val KEY_ANIME_ID = "animeId"
        const val KEY_NOTIFICATION_ID = "notificationId"

    }
}

fun getWorkId(animeId: String) = "anime_notification_$animeId"

fun scheduleAnimeNotification(
    context: Context,
    animeId: Int,
    notificationId: Int,
    delaySeconds: Long = 0L
): Operation {
    val input = Data.Builder()
        .putInt(NotificationScheduleWorker.KEY_ANIME_ID, animeId)
        .putInt(NotificationScheduleWorker.KEY_NOTIFICATION_ID, notificationId)
        .build()

    val request = OneTimeWorkRequestBuilder<NotificationScheduleWorker>()
        .setInputData(input)
        .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
        .build()

    return WorkManager.getInstance(context).enqueueUniqueWork(
        "anime_notification_$animeId",
        ExistingWorkPolicy.REPLACE,
        request
    )

}

