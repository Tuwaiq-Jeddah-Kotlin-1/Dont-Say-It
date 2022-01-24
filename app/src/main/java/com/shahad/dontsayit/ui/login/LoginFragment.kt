package com.shahad.dontsayit.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.network.ViewModel
import com.shahad.dontsayit.databinding.FragmentLoginBinding
import com.shahad.dontsayit.util.checkConnection
import com.shahad.dontsayit.util.checkIfEmpty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var viewModel: ViewModel
    private lateinit var scaleDown: Animation
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]

        binding.etEmail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.emaillayout.helperText = validation(binding.etEmail)
            }
        }
        binding.etPassword.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.passwordlayout.helperText = validation(binding.etPassword)
            }
        }
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
                binding.btnLogin.startAnimation(scaleDown)
                delay(100)
                if (checkConnection(
                        requireContext(),
                        viewModel.checkConnection(requireContext())
                    )
                ) {
                    if (sendToCheck()) {
                        viewModel.signIn(
                            binding.etEmail.text.toString().trim(),
                            binding.etPassword.text.toString().trim(), findNavController()
                        )
                    }
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

    }

    private fun validation(editText: EditText): String? {
        return checkIfEmpty(editText.text.toString().trim())
    }


    private fun sendToCheck(): Boolean {
        return checkIfEmpty(binding.etEmail.text.toString().trim()) == null &&
                checkIfEmpty(binding.etPassword.text.toString().trim()) == null
    }
}