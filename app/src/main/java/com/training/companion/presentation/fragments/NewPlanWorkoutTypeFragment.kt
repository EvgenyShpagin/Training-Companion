package com.training.companion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.training.companion.R
import com.training.companion.databinding.FragmentNewPlanWorkoutTypeBinding
import com.training.companion.presentation.util.nameStringRes
import com.training.companion.presentation.viewmodels.NewPlanTypeViewModel
import com.training.companion.presentation.viewmodels.factories.WorkoutTypeChoiceFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewPlanWorkoutTypeFragment : Fragment() {

    private var _binding: FragmentNewPlanWorkoutTypeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlanTypeViewModel by viewModels { WorkoutTypeChoiceFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlanWorkoutTypeBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            viewModel.unfinishedPlanWorkoutType.collectLatest { workoutType ->
                workoutType?.let {
                    MaterialAlertDialogBuilder(requireContext())
                        .setCancelable(false)
                        .setTitle(R.string.dialog_title_plan_being_created_exists)
                        .setMessage(
                            getString(
                                R.string.dialog_message_plan_being_created_exists,
                                getString(it.nameStringRes)
                            )
                        )
                        .setPositiveButton(R.string.resume) { _, _ ->
                            viewModel.resumePrevPlanCreation(binding.root)
                        }.setNegativeButton(R.string.dialog_button_text_create_new) { _, _ ->
                            viewModel.deletePrevPlan()
                        }
                        .show()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}