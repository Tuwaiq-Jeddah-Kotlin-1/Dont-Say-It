package com.shahad.dontsayit.ui.main

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.shahad.dontsayit.*
import com.shahad.dontsayit.data.network.ViewModel
import com.shahad.dontsayit.notification.WorkerNotification
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]

        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        sharedPreferences = this.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)

        notification()
        sharedPreferences = this.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val emailPref = sharedPreferences.getString(EMAIL, null)
        if (emailPref != null) {
            graph.startDestination = R.id.homeFragment
        } else {
            graph.startDestination = R.id.loginFragment
        }
        navHostFragment.navController.graph = graph
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