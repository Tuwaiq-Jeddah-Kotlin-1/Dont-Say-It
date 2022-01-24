package com.shahad.dontsayit.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.shahad.dontsayit.CHANNEL_ID

class AppLifeCycle : Application() {

    override fun onCreate() {//when app created
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//check version(oreo and higher) cuz older ones require channel id?
            val name = "Dont say it Game Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

        }

    }
}