package com.shahad.dontsayit.ui.main

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.shahad.dontsayit.R
import android.app.Activity
import android.content.res.Configuration
import java.util.*


class SettingsFragment : Fragment() {
    private lateinit var etUsername: EditText
    private lateinit var btnEditUsername: ImageButton
    private lateinit var tvEmail: TextView
    private lateinit var tvSignOut: TextView
    private lateinit var engLang: TextView
    private lateinit var arLang: TextView
    private lateinit var switchTheme: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findView(view)

        engLang.setOnClickListener {
            setLocale(requireActivity(),"en")
        }
        arLang.setOnClickListener {
            setLocale(requireActivity(),"ar")

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
    }
}