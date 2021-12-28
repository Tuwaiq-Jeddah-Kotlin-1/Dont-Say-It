package com.shahad.dontsayit.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.shahad.dontsayit.*
import com.shahad.dontsayit.data.network.ViewModel
import java.util.*


class SettingsFragment : Fragment() {
    private lateinit var etUsername: EditText
    private lateinit var btnEditUsername: ImageButton
    private lateinit var tvEmail: TextView
    private lateinit var tvSignOut: TextView
    private lateinit var engLang: TextView
    private lateinit var arLang: TextView
    private lateinit var switchTheme: SwitchCompat
    private lateinit var viewModel: ViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private val auth = FirebaseAuth.getInstance()
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findView(view)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        username = sharedPreferences.getString(USERNAME, "") ?: ""

        if (sharedPreferences.getBoolean(DARKTHEME,false)) {//if dark theme is true flip the switch
          //  Toast.makeText(requireContext(),"SettingsFragment MODE_NIGHT_YES", Toast.LENGTH_SHORT).show()

            switchTheme.isChecked = true
        }
        etUsername.setText(username)
        etUsername.isEnabled = false


        tvEmail.text = sharedPreferences.getString(EMAIL, "")

        btnEditUsername.setOnClickListener {
            etUsername.isEnabled = !etUsername.isEnabled
            etUsername.requestFocus()
            if (etUsername.isEnabled) {
                btnEditUsername.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)

            } else {
                btnEditUsername.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit)
            }
            if (etUsername.text.toString() != username) {
                //update db with new username
                username = etUsername.text.toString()
                viewModel.updateUser(username)
                sharedPreferences.edit().putString(USERNAME, username).apply()
            }
        }
        engLang.setOnClickListener {//save in preference and start app with it, get words with it
            sharedPreferences.edit().putString(LANG,"en").apply()
            setLocale(requireActivity(), "en")
        }
        arLang.setOnClickListener {
            sharedPreferences.edit().putString(LANG,"ar").apply()
            setLocale(requireActivity(), "ar")
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->//save in preference and start app with it
            /*if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)

            } else {

                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }*/

            if (isChecked) {
                sharedPreferences.edit().putBoolean(DARKTHEME,true).apply()
              //  Toast.makeText(requireContext(),"isChecked SettingsFragment MODE_NIGHT_YES", Toast.LENGTH_SHORT).show()

                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
               //startActivity(Intent(requireContext(), SplashActivity::class.java))

            } else {
                sharedPreferences.edit().putBoolean(DARKTHEME,false).apply()
              //  Toast.makeText(requireContext(),"else isChecked SettingsFragment MODE_NIGHT_NO", Toast.LENGTH_SHORT).show()

                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
               //startActivity(Intent(requireContext(), SplashActivity::class.java))
            }
        }

        tvSignOut.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            auth.signOut()
            findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
        }

    }

    private fun findView(view: View) {
        etUsername = view.findViewById(R.id.etUsername)
        btnEditUsername = view.findViewById(R.id.btnEditUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvSignOut = view.findViewById(R.id.tvSignOut)
        engLang = view.findViewById(R.id.engLang)
        arLang = view.findViewById(R.id.arLang)
        switchTheme = view.findViewById(R.id.switchTheme)
    }

    private fun setLocale(activity: Activity, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        startActivity(Intent(requireContext(), MainActivity::class.java))
        activity.finish();
    }
}
    ////////////////theme practice/////////////

    /*class activity:AppCompatActivity() {
        private lateinit var switchTheme: Switch
        private lateinit var Preferences: SharedPreferences
        override fun onCreate(savedInstanceState: Bundle?) {
            Preferences=this.getSharedPreferences(PREFERENCE, MODE_PRIVATE)

            if (Preferences.getBoolean(DARKTHEME,false)) {
                setTheme(R.style.DarkTheme)
            } else {
                setTheme(R.style.AppTheme)
            }
            if (Preferences.getBoolean(DARKTHEME,false)) {
                switchTheme.isChecked = true
            }
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            switchTheme = findViewById<View>(R.id.enableDark) as Switch


            switchTheme.setOnCheckedChangeListener { button, isCheked ->
                if (isCheked) {
                    Preferences.edit().putBoolean(DARKTHEME,true).apply()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                } else {
                    Preferences.edit().putBoolean(DARKTHEME,false).apply()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }

            }

        }
    }*/


