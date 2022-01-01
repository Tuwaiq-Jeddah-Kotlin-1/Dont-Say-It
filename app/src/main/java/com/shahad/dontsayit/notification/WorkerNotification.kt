package com.shahad.dontsayit.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.shahad.dontsayit.CHANNEL_ID
import com.shahad.dontsayit.NOTIFICATION_NAME

class WorkerNotification(private val context: Context, workParams: WorkerParameters) :
    Worker(context, workParams) {
    override fun doWork(): Result {
        val notification =
            NotificationCompat.Builder(context, CHANNEL_ID).setTicker(NOTIFICATION_NAME)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(NOTIFICATION_NAME)
                .setContentText("")
                .setAutoCancel(true)//when we click it, it gets dismissed
                .build()
        //create notification manager
        val notificationManager = NotificationManagerCompat.from(context)

        //call notification manager
        notificationManager.notify(0, notification)
        return Result.success()
    }
}