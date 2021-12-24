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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shahad.dontsayit.*
import com.shahad.dontsayit.data.network.ViewModel
import java.util.*

class SignupFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: ViewModel
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var btnLogin: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findView(view)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
       /* sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val emailPref = sharedPreferences.getString(EMAIL, null)
*/
        btnSignup.setOnClickListener {

            if (validate()) {
                viewModel.signUp(
                    etEmail.text.toString().trim().lowercase(Locale.getDefault()),
                    etPassword.text.toString().trim(),
                    etUsername.text.toString().trim(),findNavController()
                )
              /*  if (emailPref != null ) {
                Toast.makeText(view.context, emailPref, Toast.LENGTH_LONG).show()

                findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
                }*/
            }
        }
        btnLogin.setOnClickListener {
            Log.i("Register: ", "nav to login")
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)

        }
    }

    private fun findView(view:View) {
        etUsername = view.findViewById(R.id.etUsername)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnSignup = view.findViewById(R.id.btnSignup)
        btnLogin = view.findViewById(R.id.btnLogin)
    }

    private fun validate(): Boolean {
        //use different types to check while user typing
        return etUsername.validateUsername(etUsername.text.toString().trim()) &&
                etEmail.validateMail(etEmail.text.toString().trim().lowercase(Locale.getDefault())) &&
                etPassword.validatePasswords(etPassword.text.toString().trim())
    }

}