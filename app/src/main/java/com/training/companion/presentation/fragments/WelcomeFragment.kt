package com.training.companion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.training.companion.R


class WelcomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onStart() {
        super.onStart()
        val nextButton = requireView().findViewById<Button>(R.id.login_next_button)
        nextButton.setOnClickListener {
            findNavController().navigate(R.id.to_authFragment)
        }
    }
}