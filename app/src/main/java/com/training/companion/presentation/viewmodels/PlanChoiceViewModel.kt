package com.training.companion.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.training.companion.domain.models.WorkoutPlan
import com.training.companion.domain.usecases.GetPlanListUseCase
import com.training.companion.presentation.fragments.PlanChoiceFragmentDirections
import com.training.companion.presentation.recyclerview.adapters.PlansAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlanChoiceViewModel(
    private val getPlanListUseCase: GetPlanListUseCase,
) : ViewModel() {

    private val _plans = MutableStateFlow(emptyList<WorkoutPlan>())
    val plans = _plans.asStateFlow()

    init {
        viewModelScope.launch {
            _plans.update { getPlanListUseCase() }
        }
    }

    val clickListener = PlansAdapter.OnClickListener { view, planId ->
        val navController = view.findNavController()
        PlanChoiceFragmentDirections.toPowerWorkoutScenarioFragment(planId).also {
            navController.navigate(it)
        }
    }
}