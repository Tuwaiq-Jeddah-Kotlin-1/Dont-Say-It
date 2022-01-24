package com.shahad.dontsayit.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
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
import androidx.navigation.fragment.navArgs
import coil.load
import com.shahad.dontsayit.*
import com.shahad.dontsayit.Util.checkConnection
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.network.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SignupFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: ViewModel
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignup: ImageButton
    private lateinit var btnLogin: TextView
    private lateinit var imgprofile: ImageView
    private lateinit var imgbtnprofile: TextView
    private lateinit var pictureList: Array<ProfilePicture>
    private lateinit var scaleDown: Animation
    private var chosenPic: String? = null
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
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)

        val args: SignupFragmentArgs? by navArgs()
        args?.let {
            if (args?.picUrl != "null") {
                chosenPic = args?.picUrl
                imgprofile.load(chosenPic)
            }
            if (args?.signupdata!=null){
               etUsername.setText( args?.signupdata!![0])
               etEmail.setText( args?.signupdata!![1])
               etPassword.setText( args?.signupdata!![2])
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

        imgbtnprofile.setOnClickListener {
            if (this::pictureList.isInitialized) {
                val action =
                    SignupFragmentDirections.actionSignupFragmentToBottomSheetProfilePictures(
                        pictureList
                  , arrayOf(etUsername.text.toString(),etEmail.text.toString(),etPassword.text.toString())  )




                findNavController().navigate(action)
            }
        }
        btnSignup.setOnClickListener {
            lifecycleScope.launch {
                btnLogin.startAnimation(scaleDown)
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
                            etEmail.text.toString().trim().lowercase(Locale.getDefault()),
                            etPassword.text.toString().trim(),
                            etUsername.text.toString().trim(), findNavController()
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "choose a profile picture",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }}
        }
        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)

        }
    }


    private fun findView(view: View) {
        etUsername = view.findViewById(R.id.etUsername)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnSignup = view.findViewById(R.id.btnSignup)
        btnLogin = view.findViewById(R.id.btnLogin)
        imgprofile = view.findViewById(R.id.imgprofile)
        imgbtnprofile = view.findViewById(R.id.imgbtnprofile)
        scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)

    }

    private fun validate(): Boolean {
        //use different types to check while user typing
        return etUsername.validateUsername(etUsername.text.toString().trim()) &&
                etEmail.validateMail(
                    etEmail.text.toString().trim().lowercase(Locale.getDefault())
                ) &&
                etPassword.validatePasswords(etPassword.text.toString().trim())
    }

}