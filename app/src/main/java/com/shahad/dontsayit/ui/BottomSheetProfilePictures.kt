package com.shahad.dontsayit.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.shahad.dontsayit.PIC
import com.shahad.dontsayit.PREFERENCE
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.network.ViewModel
import com.shahad.dontsayit.databinding.BottomSheetProfilePicturesBinding

class BottomSheetProfilePictures : BottomSheetDialogFragment(), PictureAdapter.ItemListener {
    private lateinit var viewModel: ViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var data: Array<String>? = null
    private lateinit var binding: BottomSheetProfilePicturesBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetProfilePicturesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        binding.recyclerviewprofile.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val args: BottomSheetProfilePicturesArgs by navArgs()
        args.let {
            binding.recyclerviewprofile.adapter = PictureAdapter(args.picUrlList, this)
            data = args.signupdata
        }
    }

    override fun onItemClick(item: String?) {
        item?.let {
            //save in db
            //send it back to saved or updated setting or sign up
            sharedPreferences.edit().putString(PIC, it).apply()
            if (FirebaseAuth.getInstance().currentUser != null) {//in case of settings update user
                viewModel.updateProfilePic(it)
                findNavController().navigate(R.id.action_bottomSheetProfilePictures_to_settingsFragment)

            } else {// in case of sign up send it back
                val action =
                    BottomSheetProfilePicturesDirections.actionBottomSheetProfilePicturesToSignupFragment(
                        it,
                        data
                    )
                findNavController().navigate(action)

            }

        }
    }
}