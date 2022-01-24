package com.shahad.dontsayit.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.shahad.dontsayit.*
import com.shahad.dontsayit.databinding.FragmentSplashBinding
import com.shahad.dontsayit.util.setLocale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bounce: Animation
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bounce = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)

        //get theme and lang from shared preference
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val emailPref = sharedPreferences.getString(EMAIL, null)

        if (sharedPreferences.getString(LANG, "en") == "ar") {
            setLocale("ar", requireActivity(), requireContext(), false)
        } else {
            setLocale("en", requireActivity(), requireContext(), false)
        }

        if (sharedPreferences.getBoolean(DARK_THEME, false)) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        }



        lifecycleScope.launch {
            binding.logo.startAnimation(bounce)
            delay(2000)
            if (emailPref != null) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)

            }
        }

    }


}


