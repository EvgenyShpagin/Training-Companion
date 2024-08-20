package com.training.companion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.training.companion.R
import com.training.companion.databinding.FragmentPlanChoiceBinding
import com.training.companion.presentation.recyclerview.adapters.PlansAdapter
import com.training.companion.presentation.recyclerview.decorations.MarginItemDecoration
import com.training.companion.presentation.viewmodels.PlanChoiceViewModel
import com.training.companion.presentation.viewmodels.factories.PlanChoiceFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PlanChoiceFragment : Fragment() {

    private var _binding: FragmentPlanChoiceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlanChoiceViewModel by viewModels { PlanChoiceFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanChoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.plans.apply {
            adapter = PlansAdapter(onPlanClickListener = viewModel.clickListener)

            addItemDecoration(
                MarginItemDecoration(verticalSpace = resources.getDimension(R.dimen._32dp))
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.plans.collectLatest { list ->
                        val plansAdapter = binding.plans.adapter as PlansAdapter
                        plansAdapter.submitList(list)
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
