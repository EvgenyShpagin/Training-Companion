package com.training.companion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.training.companion.databinding.FragmentPowerWorkoutPlaceBinding
import com.training.companion.presentation.viewmodels.PlaceViewModel
import com.training.companion.presentation.viewmodels.factories.PlaceFactory


class PowerWorkoutPlaceFragment : Fragment() {

    private var _binding: FragmentPowerWorkoutPlaceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaceViewModel by viewModels { PlaceFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPowerWorkoutPlaceBinding
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