package com.shahad.dontsayit.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.shahad.dontsayit.*
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.network.ViewModel
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
    private lateinit var pictureList: Array<ProfilePicture>//
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
           if (args?.picUrl!="null") {
               chosenPic = args?.picUrl
               imgprofile.load(chosenPic)
           }
        }



        if (viewModel.checkConnection(requireContext()) && !this::pictureList.isInitialized) {
            viewModel.getProfilePictures().observe(viewLifecycleOwner, {
                Toast.makeText(requireContext(), "observe", Toast.LENGTH_SHORT).show()
                pictureList = it.toTypedArray()
                Toast.makeText(requireContext(), "done observe", Toast.LENGTH_SHORT).show()
            })
        }
        else {
            Toast.makeText(
                requireContext(),
                "No Internet Connect Can't load profile pictures",
                Toast.LENGTH_SHORT
            ).show()

        }
        imgbtnprofile.setOnClickListener {
            if (this::pictureList.isInitialized) {
                val action =
                    SignupFragmentDirections.actionSignupFragmentToBottomSheetProfilePictures(
                        pictureList
                    )
                findNavController().navigate(action)
            }
        }
        btnSignup.setOnClickListener {
            if (viewModel.checkConnection(requireContext())) {
                if (validate()) {
                    if (chosenPic!=null) {

                        viewModel.signUp(
                            chosenPic!!,
                            etEmail.text.toString().trim().lowercase(Locale.getDefault()),
                            etPassword.text.toString().trim(),
                            etUsername.text.toString().trim(), findNavController()
                        )
                    }else{
                        Toast.makeText(requireContext(),"choose a profile picture",Toast.LENGTH_SHORT).show()
                    }

                }
            } else {
                Toast.makeText(requireContext(), "No Internet Connect", Toast.LENGTH_SHORT).show()

            }
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