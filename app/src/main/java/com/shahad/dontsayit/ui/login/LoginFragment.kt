package com.shahad.dontsayit.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.shahad.dontsayit.R
import com.shahad.dontsayit.Util.checkConnection
import com.shahad.dontsayit.checkIfEmpty
import com.shahad.dontsayit.data.network.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var viewModel: ViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private val auth = FirebaseAuth.getInstance()

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: ImageButton
    private lateinit var btnRegister: TextView
    private lateinit var scaleDown: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findView(view)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]

        btnLogin.setOnClickListener {
            lifecycleScope.launch {
                btnLogin.startAnimation(scaleDown)
                delay(100)
            if (checkConnection(
                    requireContext(),
                    viewModel.checkConnection(requireContext())
                )
            ){
                if (sendToCheck()) {
                    viewModel.signIn(
                        etEmail.text.toString().trim(),
                        etPassword.text.toString().trim(), findNavController()
                    )
                }
            }
        }}

        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

    }

    private fun findView(view: View) {
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnRegister = view.findViewById(R.id.btnRegister)
        scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)

    }

    private fun sendToCheck(): Boolean {
        return etEmail.checkIfEmpty(etEmail.text.toString().trim()) &&
                etPassword.checkIfEmpty(etPassword.text.toString().trim())
    }
}