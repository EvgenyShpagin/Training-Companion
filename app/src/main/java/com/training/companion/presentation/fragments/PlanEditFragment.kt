package com.training.companion.presentation.fragments

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
import com.google.android.material.transition.MaterialSharedAxis
import com.training.companion.R
import com.training.companion.databinding.FragmentPlanEditBinding
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.recyclerview.adapters.PlanExerciseAdapter
import com.training.companion.presentation.util.setEnabledWithTint
import com.training.companion.presentation.viewmodels.PlanEditViewModel
import com.training.companion.presentation.viewmodels.factories.PlanEditFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlanEditFragment : Fragment() {

    private val args: PlanEditFragmentArgs by navArgs()

    private var _binding: FragmentPlanEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlanEditViewModel by viewModels {
        PlanEditFactory(args.setBuilder as SetDetailsBuilder.OfPlanEdit)
    }
    private lateinit var adapter: PlanExerciseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(material_motion_duration_long_1).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(material_motion_duration_long_1).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlanEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        adapter = PlanExerciseAdapter(requireContext(), viewModel.onExerciseClickListener)

        binding.planExercises.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.planExercises.collectLatest { list ->
                        adapter.submitList(list)
                        binding.root.doOnPreDraw { startPostponedEnterTransition() }
                    }
                }
                launch {
                    viewModel.currentListState.collectLatest { state ->
                        adapter.onStateChange(state)
                    }
                }
                launch {
                    viewModel.menuSaveButtonEnabled.collectLatest { enabled ->
                        val saveItem = binding.toolbar.menu.findItem(R.id.save_plan)
                        saveItem.setEnabledWithTint(enabled)
                    }
                }
            }
        }

        postponeEnterTransition()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}