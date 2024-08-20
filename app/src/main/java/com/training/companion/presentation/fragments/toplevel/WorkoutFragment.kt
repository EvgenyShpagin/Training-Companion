package com.training.companion.presentation.fragments.toplevel

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialContainerTransform
import com.training.companion.R
import com.training.companion.databinding.FragmentWorkoutBinding
import com.training.companion.presentation.viewmodels.WorkoutViewModel
import com.training.companion.presentation.viewmodels.factories.WorkoutFactory


class WorkoutFragment : Fragment() {

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    private var actionsButtonToCardTransform: MaterialContainerTransform? = null
    private var actionsCardToButtonTransform: MaterialContainerTransform? = null

    private val viewModel: WorkoutViewModel by viewModels { WorkoutFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.isWorkoutInProgress()) {
            navigateToWorkoutPreparation()
        }
    }

    private fun navigateToWorkoutPreparation() {
        val navController = findNavController()
        navController.navigate(R.id.startWorkoutFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.actionsButton.setOnClickListener {
            showActionsContainer()
        }
        binding.actionsCardView.setOnCloseListener {
            hideActionsContainer()
        }
        binding.actionsCardView.setOnActionClickListener {
            viewModel.onActionClick(view, it)
        }
        initActionsTransform()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initActionsTransform() {
        actionsButtonToCardTransform = MaterialContainerTransform().apply {
            startView = binding.actionsButton
            endView = binding.actionsCardView
            addTarget(binding.actionsCardView)
            scrimColor = Color.TRANSPARENT
            duration = SHOW_ACTIONS_DURATION
        }
        actionsCardToButtonTransform = MaterialContainerTransform().apply {
            startView = binding.actionsCardView
            endView = binding.actionsButton
            scrimColor = Color.TRANSPARENT
            addTarget(binding.actionsButton)
            duration = HIDE_ACTIONS_DURATION
        }
    }

    private fun showActionsContainer() {
        if (binding.actionsCardView.isVisible) return
        binding.actionsCardView.setContentVisible()
        TransitionManager.beginDelayedTransition(binding.root, actionsButtonToCardTransform)
        binding.actionsButton.isInvisible = true
        binding.actionsCardView.isVisible = true
    }

    private fun hideActionsContainer() {
        if (!binding.actionsCardView.isVisible) return
        binding.actionsCardView.setContentInvisible()
        TransitionManager.beginDelayedTransition(
            binding.root as ViewGroup,
            actionsCardToButtonTransform
        )
        binding.actionsButton.isVisible = true
        binding.actionsCardView.isInvisible = true
    }

    companion object {
        private const val SHOW_ACTIONS_DURATION = 600L
        private const val HIDE_ACTIONS_DURATION = 400L
    }
}