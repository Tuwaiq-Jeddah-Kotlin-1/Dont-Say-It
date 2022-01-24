package com.shahad.dontsayit.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.shahad.dontsayit.R
import com.shahad.dontsayit.notification.WorkerNotification
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notification()
    }

    private fun notification() {
        val periodicWorker = PeriodicWorkRequest.Builder(
            WorkerNotification::class.java, 2, TimeUnit.DAYS//minimum 15 min
        ).build()//made worker with periodic time repeat

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "periodicNotification",
            ExistingPeriodicWorkPolicy.KEEP,//change to replace so its not every 2 days but every inactive 2 day
            periodicWorker
        ) //unique work


    }

}