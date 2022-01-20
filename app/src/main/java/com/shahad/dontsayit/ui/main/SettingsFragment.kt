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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.shahad.dontsayit.*
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.network.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.util.*


class SettingsFragment : Fragment(){//}, PictureAdapter.ItemListener {
    //private lateinit var recyclerview: RecyclerView//
    private lateinit var etUsername: EditText
    private lateinit var btnEditUsername: ImageButton
    private lateinit var back: ImageButton
    private lateinit var imgbtnprofile: TextView
    private lateinit var imgprofile: ImageView
    private lateinit var tvEmail: TextView
    private lateinit var imgbtnSignOut: ImageButton
    private lateinit var engLang: MaterialButton
    private lateinit var arLang: MaterialButton
    private lateinit var toggleDark: MaterialButton
    private lateinit var toggleLight: MaterialButton
   // private lateinit var switchTheme: MaterialButtonToggleGroup
    private lateinit var viewModel: ViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private val auth = FirebaseAuth.getInstance()
    private lateinit var username: String
    private lateinit var pictureList: Array<ProfilePicture>//
    private lateinit var shake: Animation
    private lateinit var scaleDown: Animation

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
        tvEmail.text = sharedPreferences.getString(EMAIL, "")

    imgprofile.load(sharedPreferences.getString(PIC, null))



        etUsername.setText(username)
        etUsername.isEnabled = false
        if (sharedPreferences.getBoolean(DARK_THEME, false)) {//if dark theme is true flip the switch
            toggleDark.isChecked = true
        }else{
            toggleLight.isChecked = true
        }
    if (sharedPreferences.getString (LANG,"en")=="en") {//if dark theme is true flip the switch
            engLang.isChecked = true
        }else{
        arLang.isChecked = true

    }
        if(viewModel.checkConnection(requireContext())&& !this::pictureList.isInitialized){
        viewModel.getProfilePictures().observe(viewLifecycleOwner,{
            Toast.makeText(requireContext(),"observe",Toast.LENGTH_SHORT).show()
            pictureList=it.toTypedArray()
            Toast.makeText(requireContext(),"done observe",Toast.LENGTH_SHORT).show()
        })}

        btnEditUsername.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                btnEditUsername.startAnimation(shake)
                delay(100)

                etUsername.isEnabled = !etUsername.isEnabled
                etUsername.requestFocus()
                if (etUsername.isEnabled) {
                    btnEditUsername.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.check)

                } else {
                    btnEditUsername.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.draw)
                }
                if (etUsername.text.toString() != username) {
                    //update db with new username
                    username = etUsername.text.toString()
                    viewModel.updateUsername(username)
                    sharedPreferences.edit().putString(USERNAME, username).apply()
                }
            }
        }

        back.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)

        }
        engLang.setOnClickListener {//save in preference and start app with it, get words with it
            sharedPreferences.edit().putString(LANG, "en").apply()
            setLocale( "en")
        }
        arLang.setOnClickListener {
            sharedPreferences.edit().putString(LANG, "ar").apply()
            setLocale( "ar")
        }
        toggleDark.setOnClickListener {
            sharedPreferences.edit().putBoolean(DARK_THEME, true).apply()
            //  Toast.makeText(requireContext(),"isChecked SettingsFragment MODE_NIGHT_YES", Toast.LENGTH_SHORT).show()
           // resources.configuration.uiMode = Configuration.UI_MODE_NIGHT_YES

            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            //startActivity(Intent(requireContext(), SplashActivity::class.java))
           // recreate(context as Activity)

        }

        toggleLight.setOnClickListener {
            sharedPreferences.edit().putBoolean(DARK_THEME, false).apply()
            //  Toast.makeText(requireContext(),"else isChecked SettingsFragment MODE_NIGHT_NO", Toast.LENGTH_SHORT).show()
          //  resources.configuration.uiMode = Configuration.UI_MODE_NIGHT_NO
           AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
           // recreate(context as Activity)

        }
       /* switchTheme.setOnCheckedChangeListener { _, isChecked ->//save in preference and start app with it
            *//*if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)

            } else {

                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }*//*

            if (isChecked) {
                sharedPreferences.edit().putBoolean(DARK_THEME, true).apply()
                //  Toast.makeText(requireContext(),"isChecked SettingsFragment MODE_NIGHT_YES", Toast.LENGTH_SHORT).show()

                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                //startActivity(Intent(requireContext(), SplashActivity::class.java))

            } else {
                sharedPreferences.edit().putBoolean(DARK_THEME, false).apply()
                //  Toast.makeText(requireContext(),"else isChecked SettingsFragment MODE_NIGHT_NO", Toast.LENGTH_SHORT).show()

                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                //startActivity(Intent(requireContext(), SplashActivity::class.java))
            }
        }*/

        imgbtnprofile.setOnClickListener {
            //show bottomsheet
            if (this::pictureList.isInitialized) {
                val action =
                    SettingsFragmentDirections.actionSettingsFragmentToBottomSheetProfilePictures(
                        pictureList
                    )
                findNavController().navigate(action)
            }
        }

        imgbtnSignOut.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                imgbtnSignOut.startAnimation(scaleDown)
                delay(100)

                sharedPreferences.edit().clear().apply()
            auth.signOut()
            findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
        }}

    }

    private fun findView(view: View) {
        etUsername = view.findViewById(R.id.etUsername)
        btnEditUsername = view.findViewById(R.id.btnEditUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        imgbtnSignOut = view.findViewById(R.id.imgbtnSignOut)
        engLang = view.findViewById(R.id.engLang)
        arLang = view.findViewById(R.id.arLang)
        toggleLight = view.findViewById(R.id.toggleLight)
        toggleDark = view.findViewById(R.id.toggleDark)
       // switchTheme = view.findViewById(R.id.switchTheme)
        imgbtnprofile = view.findViewById(R.id.imgbtnprofile)
        imgprofile = view.findViewById(R.id.imgprofile)
        back = view.findViewById(R.id.back)
        shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
        scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)

    }



    private fun setLocale(localeName: String) {
        val locale = Locale(localeName)
        Locale.setDefault(locale)
        val resources = activity?.resources
        val config: Configuration = resources!!.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        /* startActivity(Intent(requireContext(), MainActivity::class.java))
         activity.finish();*/
        recreate(context as Activity)

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


