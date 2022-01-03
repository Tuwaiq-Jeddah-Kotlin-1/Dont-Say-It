package com.shahad.dontsayit.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.shahad.dontsayit.PIC
import com.shahad.dontsayit.PREFERENCE
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.network.ViewModel

class BottomSheetProfilePictures : BottomSheetDialogFragment(), PictureAdapter.ItemListener {
    private lateinit var recyclerview: RecyclerView//
   // private lateinit var pictureList: Array<ProfilePicture> //
    private lateinit var viewModel: ViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_profile_pictures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview = view.findViewById(R.id.recyclerviewprofile)//
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)

        recyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)//


        /* viewModel.getProfilePictures().observe(viewLifecycleOwner, {
             Toast.makeText(requireContext(), "observe", Toast.LENGTH_SHORT).show()
             pictureList = it
             Toast.makeText(requireContext(), "done observe", Toast.LENGTH_SHORT).show()

         })*/

        val args: BottomSheetProfilePicturesArgs by navArgs()
        args.let {
           // pictureList = args.picUrlList
            recyclerview.adapter = PictureAdapter(args.picUrlList, this)
        }


    }

    override fun onItemClick(item: String?) {
        // Snackbar.make(coordinatorLayout!!, "$item is selected", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        item?.let {
            //save in db
            Toast.makeText(requireContext(), "it: $it", Toast.LENGTH_LONG).show()
            //send it back to saved or updated setting or sign up
            if (FirebaseAuth.getInstance().currentUser != null) {//in case of settings update user
                sharedPreferences.edit().putString(PIC, it).apply()
                viewModel.updateProfilePic(it)
                findNavController().navigate(R.id.action_bottomSheetProfilePictures_to_settingsFragment)

            } else {// in case of sign up send it back
                sharedPreferences.edit().putString(PIC, it).apply()
                val action =
                    BottomSheetProfilePicturesDirections.actionBottomSheetProfilePicturesToSignupFragment(
                        it
                    )
                findNavController().navigate(action)
// findNavController().navigate(R.id.action_bottomSheetProfilePictures_to_settingsFragment)

            }
            //viewModel.updateProfilePic(it)
           // sharedPreferences.edit().putString(PIC, it).apply()
            // findNavController().navigate(R.id.action_bottomSheetProfilePictures_to_settingsFragment)

            //val action= BottomSheetProfilePicturesDirections.actionBottomSheetProfilePicturesToSettingsFragment(item)
            //   imgprofile.load(it)
            // bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }
}