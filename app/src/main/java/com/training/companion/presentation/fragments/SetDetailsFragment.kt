package com.training.companion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.training.companion.databinding.FragmentSetDetailsBinding
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.base.SetDetailsViewModel
import com.training.companion.presentation.viewmodels.factories.SetDetailsPlanEditFactory
import com.training.companion.presentation.viewmodels.factories.SetDetailsWorkoutSetFactory


class SetDetailsFragment : Fragment() {

    private var _binding: FragmentSetDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: SetDetailsFragmentArgs by navArgs()

    private val viewModel: SetDetailsViewModel by viewModels {
        when (args.setBuilder) {
            is SetDetailsBuilder.OfPlanEdit ->
                SetDetailsPlanEditFactory(args.setBuilder as SetDetailsBuilder.OfPlanEdit)

            is SetDetailsBuilder.OfWorkoutSet ->
                SetDetailsWorkoutSetFactory(args.setBuilder as SetDetailsBuilder.OfWorkoutSet)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSetDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}