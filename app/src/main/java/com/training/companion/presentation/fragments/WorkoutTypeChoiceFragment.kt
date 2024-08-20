package com.training.companion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.training.companion.databinding.FragmentWorkoutTypeChoiceBinding
import com.training.companion.presentation.viewmodels.TrainingTypeViewModel
import com.training.companion.presentation.viewmodels.factories.TrainingTypeFactory


class WorkoutTypeChoiceFragment : Fragment() {

    private var _binding: FragmentWorkoutTypeChoiceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TrainingTypeViewModel by viewModels { TrainingTypeFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutTypeChoiceBinding
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