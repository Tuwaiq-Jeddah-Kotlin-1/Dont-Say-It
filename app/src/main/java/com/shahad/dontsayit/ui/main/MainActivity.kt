package com.shahad.dontsayit.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.shahad.dontsayit.R
import com.shahad.dontsayit.WorkerNotification
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notification()
    }


    private fun notification(){
        val periodicWorker = PeriodicWorkRequest.Builder(
            WorkerNotification::class.java, 2, TimeUnit.DAYS//minimum 15 min
        ).build()//mad worker with periodic time repeat

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "periodicNotification",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorker
        )//unique work
    }


}