package com.shahad.dontsayit.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.shahad.dontsayit.R

class HowToPlayFragment : Fragment() {
    private lateinit var tvHow: TextView
    private lateinit var back: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_how_to_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvHow = view.findViewById(R.id.tvHow)
        back = view.findViewById(R.id.back)
        back.setOnClickListener {
            findNavController().navigate(R.id.action_howToPlayFragment_to_homeFragment)
        }

    }

}