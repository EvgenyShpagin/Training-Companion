package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.training.companion.domain.models.WorkoutPlan
import com.training.companion.domain.usecases.DeleteIncompletePlanExercisesUseCase
import com.training.companion.domain.usecases.GetPlanListUseCase
import com.training.companion.presentation.fragments.toplevel.PlansFragmentDirections
import com.training.companion.presentation.recyclerview.adapters.PlansAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlansViewModel(
    private val getPlanListUseCase: GetPlanListUseCase,
    private val deleteIncompletePlanExercises: DeleteIncompletePlanExercisesUseCase,
) : ViewModel() {

    private val _plans = MutableStateFlow(emptyList<WorkoutPlan>())
    val plans = _plans.asStateFlow()

    init {
        viewModelScope.launch {
            _plans.update { getPlanListUseCase() }
            deleteIncompletePlanExercises.invoke()
        }
    }

    val clickListener = PlansAdapter.OnClickListener { view, planId ->
        val fragmentNavigatorExtras = FragmentNavigatorExtras(view to "details")
        navigate(
            view = view,
            direction = PlansFragmentDirections.toPlanDetailsFragment(planId),
            extras = fragmentNavigatorExtras
        )
    }

    fun onCreateClick(view: View) {
        val action = PlansFragmentDirections.toTypeFragment()
        view.findNavController().navigate(action)
    }

    private fun navigate(view: View, direction: NavDirections, extras: Navigator.Extras) {
        view.findNavController().navigate(direction, extras)
    }
}