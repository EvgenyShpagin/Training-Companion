package com.training.companion.presentation.fragments

import android.graphics.Color
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
import androidx.navigation.fragment.navArgs
import com.google.android.material.R.integer.material_motion_duration_long_1
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.training.companion.R
import com.training.companion.databinding.FragmentPlanDetailsBinding
import com.training.companion.presentation.recyclerview.adapters.PlanExerciseAdapter
import com.training.companion.presentation.viewmodels.PlanDetailsViewModel
import com.training.companion.presentation.viewmodels.factories.PlanDetailsFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlanDetailsFragment : Fragment() {

    private val args: PlanDetailsFragmentArgs by navArgs()

    private var _binding: FragmentPlanDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlanDetailsViewModel by viewModels { PlanDetailsFactory(args.planId) }
    private lateinit var adapter: PlanExerciseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.main_fragment_container
            duration = resources.getInteger(material_motion_duration_long_1).toLong()
            scrimColor = Color.TRANSPARENT
            startContainerColor = requireContext().getColor(R.color.chinese_black)
            endContainerColor = requireContext().getColor(R.color.cod_gray)
        }

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(material_motion_duration_long_1).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(material_motion_duration_long_1).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlanDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        adapter = PlanExerciseAdapter(requireContext(), null)
        binding.exercisesForPlan.adapter = adapter

        postponeEnterTransition()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    uiState.plan?.let {
                        adapter.submitList(it.planExercises)
                        view.doOnPreDraw { startPostponedEnterTransition() }
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