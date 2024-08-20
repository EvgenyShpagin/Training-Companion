package com.training.companion.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.training.companion.databinding.BottomSheetExerciseDurationBinding
import com.training.companion.domain.models.Time
import com.training.companion.presentation.viewmodels.base.SetDetailsViewModel
import com.training.companion.presentation.viewmodels.base.TimeRelatedExerciseParamViewModel
import com.training.companion.presentation.viewmodels.factories.TimeRelatedExerciseParamFactory

class ExerciseDurationBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetExerciseDurationBinding? = null
    private val binding get() = _binding!!

    private val detailsViewModel: SetDetailsViewModel by viewModels({
        requireParentFragment().childFragmentManager.fragments.first()
    })


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetExerciseDurationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: TimeRelatedExerciseParamViewModel by viewModels {
            TimeRelatedExerciseParamFactory(
                previouslyChosenTime = detailsViewModel.duration,
                defaultTime = Time(30)
            )
        }
        binding.viewModel = viewModel
    }

    override fun onStart() {
        super.onStart()
        binding.buttonSubmit.setOnClickListener { submit() }
        binding.buttonRemove.setOnClickListener { remove() }
    }

    private fun submit() {
        val chosenTime = Time(
            seconds = binding.secondsPickerDuration.value,
            minutes = binding.minutesPickerDuration.value,
            hours = 0
        )
        detailsViewModel.onDurationChosen(chosenTime)
        closeDialog()
    }

    private fun remove() {
        detailsViewModel.onDurationChosen(null)
        closeDialog()
    }

    private fun closeDialog() {
        findNavController().popBackStack()
    }

}