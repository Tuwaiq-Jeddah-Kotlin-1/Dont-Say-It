package com.shahad.dontsayit.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.shahad.dontsayit.EMAIL
import com.shahad.dontsayit.PREFERENCE
import com.shahad.dontsayit.R

class HomeFragment : Fragment() {
    private lateinit var btnCreateLobby: Button
    private lateinit var btnJoinLobby: Button
    private lateinit var btnHow: Button
    private lateinit var imgBtnShare: ImageButton
    private lateinit var imgBtnSettings: ImageButton
    private val appUrl="https://github.com/Tuwaiq-Jeddah-Kotlin-1/Dont-Say-It"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findView(view)
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val emailPref = sharedPreferences.getString(EMAIL, null)
        if (emailPref == null) {
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
        btnCreateLobby.setOnClickListener {
           // findNavController().navigate(R.id.action_homeFragment_to_gameActivity)
        }
        btnJoinLobby.setOnClickListener {
           // findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
        btnHow.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_howToPlayFragment)
        }
        imgBtnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
        imgBtnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra("Share App", appUrl)
            startActivity(intent)
        }

    }
    private fun findView(view: View) {
        btnCreateLobby = view.findViewById(R.id.btnCreateLobby)
        btnJoinLobby = view.findViewById(R.id.btnJoinLobby)
        btnHow = view.findViewById(R.id.btnHow)
        imgBtnShare = view.findViewById(R.id.imgBtnShare)
        imgBtnSettings = view.findViewById(R.id.imgBtnSettings)

    }
}