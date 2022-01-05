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
        /*  val uId = sharedPreferences.getString(UID, null)
        uId?.let { fillShared(it) } */

      /*  if (sharedPreferences.getBoolean(DARK_THEME, false)) {
            //setTheme(R.style.Theme_DontSayItNight)//.DarkTheme)
            //  Toast.makeText(this,"GameActivity MODE_NIGHT_YES", Toast.LENGTH_SHORT).show()

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            //setTheme(R.style.Theme_DontSayIt) //.AppTheme)
            //Toast.makeText(this,"GameActivity MODE_NIGHT_NO", Toast.LENGTH_SHORT).show()

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }*/
        if (sharedPreferences.getString(LANG, "en") == "ar") {
            setLocale(this, "ar")
        } else {
            setLocale(this, "en")
        }

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

    /*private fun fillShared(uId: String) {
        viewModel.getUserById(uId).observe(this, {
            Toast.makeText(this,"",Toast.LENGTH_SHORT).show()
            sharedPreferences.edit().putString(USERNAME, it.username).apply()
          //  playerName = it.username
        })
    }*/

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

    private fun setLocale(activity: Activity, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}