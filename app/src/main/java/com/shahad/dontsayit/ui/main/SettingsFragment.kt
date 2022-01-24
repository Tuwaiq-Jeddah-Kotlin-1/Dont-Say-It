package com.shahad.dontsayit.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.shahad.dontsayit.*
import com.shahad.dontsayit.Util.checkConnection
import com.shahad.dontsayit.Util.setLocale
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.network.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {
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
    private lateinit var viewModel: ViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private val auth = FirebaseAuth.getInstance()
    private lateinit var username: String
    private lateinit var pictureList: Array<ProfilePicture>
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
        etUsername.setText(username)
        etUsername.isEnabled = false
        imgprofile.load(sharedPreferences.getString(PIC, null))


        if (sharedPreferences.getBoolean(
                DARK_THEME,
                false
            )
        ) {//if dark theme is true flip the switch
            toggleDark.isChecked = true
        } else {
            toggleLight.isChecked = true
        }
        if (sharedPreferences.getString(LANG, "en") == "en") {//if lang is true flip the switch
            engLang.isChecked = true

        } else {
            arLang.isChecked = true
        }

        if (checkConnection(
                requireContext(),
                viewModel.checkConnection(requireContext())
            )
        ) {
            viewModel.getProfilePictures().observe(viewLifecycleOwner, {
                pictureList = it.toTypedArray()
            })
        }

        btnEditUsername.setOnClickListener {
            lifecycleScope.launch {
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
            setLocale("en",requireActivity(),requireContext(),true)
        }
        arLang.setOnClickListener {
            sharedPreferences.edit().putString(LANG, "ar").apply()
            setLocale("ar", requireActivity(), requireContext(),true)
        }
        toggleDark.setOnClickListener {
            sharedPreferences.edit().putBoolean(DARK_THEME, true).apply()
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            setLocale(
                sharedPreferences.getString(LANG, "en")!!,
                requireActivity(),
                requireContext(),true
            )

        }
        toggleLight.setOnClickListener {
            sharedPreferences.edit().putBoolean(DARK_THEME, false).apply()
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            setLocale(
                sharedPreferences.getString(LANG, "en")!!,
                requireActivity(),
                requireContext(),true
            )
        }
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
            lifecycleScope.launch {
                imgbtnSignOut.startAnimation(scaleDown)
                delay(100)

                sharedPreferences.edit().clear().apply()
                auth.signOut()
                findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
            }
        }
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
        imgbtnprofile = view.findViewById(R.id.imgbtnprofile)
        imgprofile = view.findViewById(R.id.imgprofile)
        back = view.findViewById(R.id.back)
        shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
        scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)

    }
}

