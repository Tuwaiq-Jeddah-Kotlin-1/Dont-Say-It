package com.shahad.dontsayit.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.shahad.dontsayit.CHANNEL_ID
import com.shahad.dontsayit.NOTIFICATION_NAME
import com.shahad.dontsayit.R

class WorkerNotification(private val context: Context, workParams: WorkerParameters) :
    Worker(context, workParams) {
    override fun doWork(): Result {
        val notification =
            NotificationCompat.Builder(context, CHANNEL_ID).setTicker(NOTIFICATION_NAME)
               .setSmallIcon(R.drawable.logo)
                .setContentTitle("Don't say it")
                .setContentText("shhhh...come play..")
                .setAutoCancel(true)//when we click it, it gets dismissed
                .build()
        //create notification manager
        val notificationManager = NotificationManagerCompat.from(context)

        //call notification manager
        notificationManager.notify(0, notification)
        return Result.success()
    }
}