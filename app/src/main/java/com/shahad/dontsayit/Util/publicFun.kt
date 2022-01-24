package com.shahad.dontsayit.Util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.shahad.dontsayit.*
import java.util.*

fun checkConnection(context: Context,boolean: Boolean):Boolean {
    return if (boolean) {
        true
    } else {
        Toast.makeText(context, "No Internet Connect", Toast.LENGTH_SHORT)
            .show()
        false

    }
}
 fun setLocale(languageCode: String, activity: Activity, context: Context, boolean: Boolean) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources = activity.resources
    val config: Configuration = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
     if (boolean){
    ActivityCompat.recreate(context as Activity)
}}
 fun savePersist(username: String, email: String, pic: String,uid:String,context: Context) {
   val sharedPreferences : SharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()
    editor.putString(EMAIL, email)
    editor.putString(UID, uid)
    editor.putString(USERNAME, username)
    editor.putString(PIC, pic)
    editor.apply()
    Log.d("sharedPreferences: ", "savePersist:success")
}

