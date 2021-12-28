package com.shahad.dontsayit.ui.main

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.shahad.dontsayit.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        sharedPreferences = this.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)

        if (sharedPreferences.getBoolean(DARKTHEME,false)) {
            //setTheme(R.style.Theme_DontSayItNight)//.DarkTheme)
          //  Toast.makeText(this,"GameActivity MODE_NIGHT_YES", Toast.LENGTH_SHORT).show()

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            //setTheme(R.style.Theme_DontSayIt) //.AppTheme)
            //Toast.makeText(this,"GameActivity MODE_NIGHT_NO", Toast.LENGTH_SHORT).show()

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        if (sharedPreferences.getString(LANG,"en")=="ar"){
            setLocale(this, "ar")
        }else{
            setLocale(this, "en")
        }

        notification()
        sharedPreferences = this.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val emailPref = sharedPreferences.getString(EMAIL, null)
        if (emailPref != null) {
            graph.startDestination = R.id.homeFragment
        }else{
            graph.startDestination = R.id.loginFragment
        }
        navHostFragment.navController.graph = graph
    }


    private fun notification(){
        val periodicWorker = PeriodicWorkRequest.Builder(
            WorkerNotification::class.java, 2, TimeUnit.DAYS//minimum 15 min
        ).build()//made worker with periodic time repeat

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "periodicNotification",
            ExistingPeriodicWorkPolicy.KEEP,
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