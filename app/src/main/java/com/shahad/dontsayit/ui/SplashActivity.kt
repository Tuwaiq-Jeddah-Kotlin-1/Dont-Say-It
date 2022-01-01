package com.shahad.dontsayit.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.shahad.dontsayit.DARK_THEME
import com.shahad.dontsayit.LANG
import com.shahad.dontsayit.PREFERENCE
import com.shahad.dontsayit.R
import com.shahad.dontsayit.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //get theme and lang from shared preference
        sharedPreferences = this.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean(DARK_THEME, false)) {
            //setTheme(R.style.Theme_DontSayItNight)//.DarkTheme)
            //   Toast.makeText(this,"SplashActivity MODE_NIGHT_YES",Toast.LENGTH_SHORT).show()
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            //setTheme(R.style.Theme_DontSayIt) //.AppTheme)
            //  Toast.makeText(this,"SplashActivity MODE_NIGHT_NO",Toast.LENGTH_SHORT).show()
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }

        if (sharedPreferences.getString(LANG, "en") == "ar") {
            setLocale(this, "ar")
        } else {
            setLocale(this, "en")
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)

            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }

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
