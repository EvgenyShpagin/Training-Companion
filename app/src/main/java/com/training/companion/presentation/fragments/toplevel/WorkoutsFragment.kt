package com.training.companion.presentation.fragments.toplevel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.training.companion.databinding.FragmentWorkoutsBinding
import com.training.companion.presentation.recyclerview.adapters.WorkoutsAdapter
import com.training.companion.presentation.util.checkNestedScrollState
import com.training.companion.presentation.viewmodels.WorkoutsViewModel
import com.training.companion.presentation.viewmodels.factories.WorkoutsFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class WorkoutsFragment : Fragment() {

    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutAdapter: WorkoutsAdapter

    private val viewModel: WorkoutsViewModel by viewModels { WorkoutsFactory() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWorkoutsBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        WorkoutsAdapter(viewModel.onWorkoutClickListener).also {
            binding.workouts.adapter = it
            workoutAdapter = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    val list = uiState.workoutList
                    workoutAdapter.submitList(list)
                    binding.root.doOnPreDraw {
                        binding.workouts.checkNestedScrollState()
                        binding.appBarLayout.setExpanded(!binding.workouts.isNestedScrollingEnabled)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}