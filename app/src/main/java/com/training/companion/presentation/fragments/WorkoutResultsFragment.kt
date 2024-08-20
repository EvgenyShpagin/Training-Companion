package com.training.companion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.training.companion.databinding.FragmentWorkoutResultsBinding
import com.training.companion.presentation.viewmodels.WorkoutResultsViewModel
import com.training.companion.presentation.viewmodels.factories.WorkoutResultsFactory

class WorkoutResultsFragment: Fragment() {

    private var _binding: FragmentWorkoutResultsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutResultsViewModel by viewModels { WorkoutResultsFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

}