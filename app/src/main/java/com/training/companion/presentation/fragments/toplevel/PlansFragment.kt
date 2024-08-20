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
import com.training.companion.R
import com.training.companion.databinding.FragmentPlansBinding
import com.training.companion.presentation.recyclerview.adapters.PlansAdapter
import com.training.companion.presentation.recyclerview.decorations.MarginItemDecoration
import com.training.companion.presentation.util.checkNestedScrollState
import com.training.companion.presentation.viewmodels.PlansViewModel
import com.training.companion.presentation.viewmodels.factories.PlansFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PlansFragment : Fragment() {

    private var _binding: FragmentPlansBinding? = null
    private val binding get() = _binding!!

    private val plansViewModel: PlansViewModel by viewModels { PlansFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = plansViewModel

        binding.plans.apply {
            adapter = PlansAdapter(plansViewModel.clickListener)

            addItemDecoration(
                MarginItemDecoration(
                    verticalSpace = resources.getDimension(R.dimen.default_margin),
                    withTopItemMargin = true
                )
            )
        }

        postponeEnterTransition()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    plansViewModel.plans.collectLatest { list ->
                        val plansAdapter = binding.plans.adapter as PlansAdapter
                        plansAdapter.submitList(list)
                        binding.plans.doOnPreDraw {
                            binding.plans.checkNestedScrollState()
                            binding.appBarLayout.setExpanded(!binding.plans.isNestedScrollingEnabled)
                            binding.root.doOnPreDraw { startPostponedEnterTransition() }
                        }
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
