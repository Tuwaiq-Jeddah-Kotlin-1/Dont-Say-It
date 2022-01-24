package com.shahad.dontsayit.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.shahad.dontsayit.*
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.network.ViewModel
import com.shahad.dontsayit.databinding.FragmentSettingsBinding
import com.shahad.dontsayit.util.checkConnection
import com.shahad.dontsayit.util.setLocale
import com.shahad.dontsayit.util.validateUsername
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private lateinit var viewModel: ViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private val auth = FirebaseAuth.getInstance()
    private lateinit var username: String
    private lateinit var pictureList: Array<ProfilePicture>
    private lateinit var shake: Animation
    private lateinit var scaleDown: Animation
    private lateinit var binding: FragmentSettingsBinding
    private val user = FirebaseAuth.getInstance().currentUser!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
        scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)

        binding.etUsername.isEnabled = false
        binding.imgprofile.load(sharedPreferences.getString(PIC,user.photoUrl.toString()))
        binding.tvEmail.text = user.email
        username = user.displayName ?: ""
        binding.etUsername.setText(username)


        if (sharedPreferences.getBoolean(
                DARK_THEME,
                false
            )
        ) {//if dark theme is true flip the switch
            binding.toggleDark.isChecked = true
        } else {
            binding.toggleLight.isChecked = true
        }
        if (sharedPreferences.getString(LANG, "en") == "en") {//if lang is true flip the switch
            binding.engLang.isChecked = true

        } else {
            binding.arLang.isChecked = true
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
        binding.etUsername.addTextChangedListener {
            binding.usernamelayout.helperText = validateUsername(binding.etUsername.text.toString())
        }
        binding.btnEditUsername.setOnClickListener {
            lifecycleScope.launch {
                binding.btnEditUsername.startAnimation(shake)
                delay(100)

                binding.etUsername.isEnabled = !binding.etUsername.isEnabled
                binding.etUsername.requestFocus()
                if (binding.etUsername.isEnabled) {
                    binding.btnEditUsername.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.check)

                } else {
                    binding.btnEditUsername.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.draw)
                }
                if (validateUsername(binding.etUsername.text.toString()) == null) {
                    binding.etUsername.text.toString()
                    username = binding.etUsername.text.toString()
                    viewModel.updateUsername(username)
                    sharedPreferences.edit().putString(USERNAME, username).apply()
                } else {
                    Toast.makeText(requireContext(), R.string.failed, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
        }
        binding.engLang.setOnClickListener {//save in preference and start app with it, get words with it
            sharedPreferences.edit().putString(LANG, "en").apply()
            setLocale("en", requireActivity(), requireContext(), true)
        }
        binding.arLang.setOnClickListener {
            sharedPreferences.edit().putString(LANG, "ar").apply()
            setLocale("ar", requireActivity(), requireContext(), true)
        }
        binding.toggleDark.setOnClickListener {
            sharedPreferences.edit().putBoolean(DARK_THEME, true).apply()
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            setLocale(
                sharedPreferences.getString(LANG, "en")!!,
                requireActivity(),
                requireContext(), true
            )

        }
        binding.toggleLight.setOnClickListener {
            sharedPreferences.edit().putBoolean(DARK_THEME, false).apply()
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            setLocale(
                sharedPreferences.getString(LANG, "en")!!,
                requireActivity(),
                requireContext(), true
            )
        }
        binding.imgprofile.setOnClickListener {
            displayBottomsheet()
        }
        binding.imgbtnprofile.setOnClickListener {
            displayBottomsheet()
        }
        binding.imgbtnSignOut.setOnClickListener {
            lifecycleScope.launch {
                binding.imgbtnSignOut.startAnimation(scaleDown)
                delay(100)
                sharedPreferences.edit().clear().apply()
                auth.signOut()
                findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
            }
        }
    }

    private fun displayBottomsheet() {
        if (this::pictureList.isInitialized) {
            val action =
                SettingsFragmentDirections.actionSettingsFragmentToBottomSheetProfilePictures(
                    pictureList
                )
            findNavController().navigate(action)
        }
    }
}

