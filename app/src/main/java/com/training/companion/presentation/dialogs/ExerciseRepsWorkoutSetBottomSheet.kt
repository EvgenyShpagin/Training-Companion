package com.training.companion.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.training.companion.databinding.BottomSheetExerciseWorkoutSetRepsBinding
import com.training.companion.domain.models.Reps
import com.training.companion.presentation.viewmodels.ExerciseRepsViewModel
import com.training.companion.presentation.viewmodels.base.SetDetailsViewModel
import com.training.companion.presentation.viewmodels.factories.RepsExerciseParamFactory
import kotlinx.coroutines.launch

class ExerciseRepsWorkoutSetBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetExerciseWorkoutSetRepsBinding? = null
    private val binding get() = _binding!!

    private val detailsViewModel: SetDetailsViewModel by viewModels({
        requireParentFragment().childFragmentManager.fragments.first()
    })

    private val viewModel: ExerciseRepsViewModel by viewModels {
        RepsExerciseParamFactory(detailsViewModel.reps, Reps.Exact(12))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetExerciseWorkoutSetRepsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.buttonSubmit.setOnClickListener {
                    detailsViewModel.onRepsChosen(viewModel.currentReps)
                    closeDialog()
                }
                binding.buttonRemove.setOnClickListener {
                    detailsViewModel.onRepsChosen(null)
                    closeDialog()
                }
            }
        }
    }

    private fun closeDialog() {
        findNavController().popBackStack()
    }
}

