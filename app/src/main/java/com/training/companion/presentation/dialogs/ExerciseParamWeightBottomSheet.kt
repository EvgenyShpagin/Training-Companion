package com.training.companion.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.training.companion.databinding.BottomSheetExerciseWeightBinding
import com.training.companion.presentation.viewmodels.WeightExerciseViewModel
import com.training.companion.presentation.viewmodels.base.SetDetailsViewModel
import com.training.companion.presentation.viewmodels.factories.WeightExerciseParamFactory

class ExerciseParamWeightBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetExerciseWeightBinding? = null
    private val binding get() = _binding!!
    private val detailsViewModel: SetDetailsViewModel by viewModels({
        requireParentFragment().childFragmentManager.fragments.first()
    })

    private val viewModel: WeightExerciseViewModel by viewModels {
        WeightExerciseParamFactory(detailsViewModel.weight)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetExerciseWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onStart() {
        super.onStart()
        binding.buttonSubmit.setOnClickListener {
            submit()
        }

        binding.buttonRemove.setOnClickListener {
            remove()
        }
    }

    private fun submit() {
        detailsViewModel.onWeightChosen(viewModel.getWeight()!!)
        closeDialog()
    }

    private fun remove() {
        detailsViewModel.onWeightChosen(null)
        closeDialog()
    }

    private fun closeDialog() {
        findNavController().popBackStack()
    }

}