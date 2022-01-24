package com.shahad.dontsayit.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.shahad.dontsayit.R
import com.shahad.dontsayit.databinding.FragmentHowToPlayBinding

class HowToPlayFragment : Fragment() {
    private lateinit var binding: FragmentHowToPlayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHowToPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_howToPlayFragment_to_homeFragment)
        }
        binding.tvHow//continue
    }

}