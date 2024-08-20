package com.training.companion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.training.companion.databinding.FragmentPowerWorkoutRestTimeBinding
import com.training.companion.presentation.viewmodels.RestTimeViewModel
import com.training.companion.presentation.viewmodels.factories.RestTimeFactory


class PowerWorkoutRestTimeFragment : Fragment() {

    private var _binding: FragmentPowerWorkoutRestTimeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RestTimeViewModel by viewModels { RestTimeFactory() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPowerWorkoutRestTimeBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}