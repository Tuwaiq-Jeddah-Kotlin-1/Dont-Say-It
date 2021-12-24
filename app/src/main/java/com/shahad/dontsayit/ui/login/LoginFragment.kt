package com.shahad.dontsayit.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.shahad.dontsayit.EMAIL
import com.shahad.dontsayit.PREFERENCE
import com.shahad.dontsayit.R
import com.shahad.dontsayit.checkIfEmpty
import com.shahad.dontsayit.data.network.ViewModel

class LoginFragment : Fragment() {

    private lateinit var viewModel: ViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private val auth = FirebaseAuth.getInstance()

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView

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
       /* sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val emailPref = sharedPreferences.getString(EMAIL, null)

        if (emailPref != null) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }*/
        btnLogin.setOnClickListener {
            if (sendToCheck()) {
                viewModel.signIn(
                    etEmail.text.toString().trim(),
                    etPassword.text.toString().trim(), findNavController()
                )
                Log.i("signin", "login checked")


                /*  if (emailPref != null) {
                  Toast.makeText(view.context, emailPref, Toast.LENGTH_LONG).show()
                  findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                  }*/

            }
        }

        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)

            Log.i("Login: ", "nav to register")
        }

    }

    private fun findView(view: View) {
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnRegister = view.findViewById(R.id.btnRegister)

    }

    private fun sendToCheck(): Boolean {
        return etEmail.checkIfEmpty(etEmail.text.toString().trim()) &&
                etPassword.checkIfEmpty(etPassword.text.toString().trim())
    }
}