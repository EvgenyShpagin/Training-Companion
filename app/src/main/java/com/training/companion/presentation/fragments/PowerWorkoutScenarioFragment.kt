package com.training.companion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.training.companion.databinding.FragmentPowerWorkoutScenarioBinding
import com.training.companion.presentation.viewmodels.ScenarioViewModel
import com.training.companion.presentation.viewmodels.factories.ScenarioFactory


class PowerWorkoutScenarioFragment : Fragment() {

    private var _binding: FragmentPowerWorkoutScenarioBinding? = null
    private val binding get() = _binding!!
    private val args: PowerWorkoutScenarioFragmentArgs by navArgs()

    private val viewModel: ScenarioViewModel by viewModels { ScenarioFactory(args.chosenPlanId) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPowerWorkoutScenarioBinding
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