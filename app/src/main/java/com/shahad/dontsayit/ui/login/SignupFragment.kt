package com.shahad.dontsayit.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.shahad.dontsayit.PREFERENCE
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.network.ViewModel
import com.shahad.dontsayit.databinding.FragmentSignupBinding
import com.shahad.dontsayit.util.checkConnection
import com.shahad.dontsayit.util.validateMail
import com.shahad.dontsayit.util.validatePasswords
import com.shahad.dontsayit.util.validateUsername
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SignupFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: ViewModel

    /*private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignup: ImageButton
    private lateinit var btnLogin: TextView
    private lateinit var imgprofile: ImageView
    private lateinit var imgbtnprofile: TextView*/
    private lateinit var pictureList: Array<ProfilePicture>
    private lateinit var scaleDown: Animation
    private var chosenPic: String? = null

    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // findView(view)
        scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)

        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)

        val args: SignupFragmentArgs? by navArgs()
        args?.let {
            if (args?.picUrl != "null") {
                chosenPic = args?.picUrl
                binding.imgprofile.load(chosenPic)
            }
            if (args?.signupdata != null) {
                binding.etUsername.setText(args?.signupdata!![0])
                binding.etEmail.setText(args?.signupdata!![1])
                binding.etPassword.setText(args?.signupdata!![2])
            }
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

        binding.etUsername.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.usernamelayout.helperText =
                    validateUsername(binding.etUsername.text.toString().trim())
            }
        }
        binding.etEmail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.emaillayout.helperText = validateMail(
                    binding.etEmail.text.toString().trim().lowercase(Locale.getDefault())
                )
            }
        }
        binding.etPassword.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.passwordlayout.helperText =
                    validatePasswords(binding.etPassword.text.toString().trim())
            }
        }
        binding.imgprofile.setOnClickListener {
            displayBottomsheet()
        }
        binding.imgbtnprofile.setOnClickListener {
            displayBottomsheet()

        }
        binding.btnSignup.setOnClickListener {
            lifecycleScope.launch {
                binding.btnSignup.startAnimation(scaleDown)
                delay(100)
                if (checkConnection(
                        requireContext(),
                        viewModel.checkConnection(requireContext())
                    )
                ) {
                    if (validate()) {
                        if (chosenPic != null) {

                            viewModel.signUp(
                                chosenPic!!,
                                binding.etEmail.text.toString().trim()
                                    .lowercase(Locale.getDefault()),
                                binding.etPassword.text.toString().trim(),
                                binding.etUsername.text.toString().trim(), findNavController()
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                R.string.chosephoto,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            }
        }
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)

        }
    }

    private fun displayBottomsheet() {


        if (checkConnection(
                requireContext(),
                viewModel.checkConnection(requireContext())
            )&&this::pictureList.isInitialized) {
            val action =
                SignupFragmentDirections.actionSignupFragmentToBottomSheetProfilePictures(
                    pictureList,
                    arrayOf(
                        binding.etUsername.text.toString(),
                        binding.etEmail.text.toString(),
                        binding.etPassword.text.toString()
                    )
                )
            findNavController().navigate(action)
        }
    }


    private fun validate(): Boolean {
        //use different types to check while user typing

        return validateUsername(
            binding.etUsername.text.toString().trim()
        ) == null &&
                validateMail(
                    binding.etEmail.text.toString().trim().lowercase(Locale.getDefault())
                ) == null &&
                validatePasswords(binding.etPassword.text.toString().trim()) == null

    }

}