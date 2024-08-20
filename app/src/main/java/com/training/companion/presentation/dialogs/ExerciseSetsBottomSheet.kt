package com.training.companion.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.training.companion.databinding.BottomSheetExerciseSetsBinding
import com.training.companion.presentation.viewmodels.base.SetDetailsViewModel


private const val DEFAULT_SETS_NUMBER = 2

class ExerciseSetsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetExerciseSetsBinding? = null
    private val binding get() = _binding!!

    private val detailsViewModel: SetDetailsViewModel by viewModels({
        requireParentFragment().childFragmentManager.fragments.first()
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetExerciseSetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.alreadyChosenSetsNumber = detailsViewModel.setsNumber ?: DEFAULT_SETS_NUMBER
    }

    override fun onStart() {
        super.onStart()
        binding.buttonSubmit.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        detailsViewModel.onSetsNumberChosen(binding.setsPicker.value)
        closeDialog()
    }

    private fun closeDialog() {
        findNavController().popBackStack()
    }

}