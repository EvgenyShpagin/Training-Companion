package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.training.companion.domain.enums.PlanEditType
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.PlanBeingCreated
import com.training.companion.domain.models.PlanUpdates
import com.training.companion.domain.usecases.DeletePlanBeingCreatedUseCase
import com.training.companion.domain.usecases.GetPlanBeingCreated
import com.training.companion.domain.usecases.IsPlanBeingCreatedEmptyUseCase
import com.training.companion.domain.usecases.SavePlanUseCase
import com.training.companion.presentation.fragments.NewPlanWorkoutTypeFragmentDirections
import com.training.companion.presentation.models.SetDetailsBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewPlanTypeViewModel(
    private val savePlan: SavePlanUseCase,
    private val getPlanBeingCreated: GetPlanBeingCreated,
    private val deletePlanBeingCreated: DeletePlanBeingCreatedUseCase,
    isPlanBeingCreatedEmpty: IsPlanBeingCreatedEmptyUseCase,
) : ViewModel() {

    private val _unfinishedPlanWorkoutType = MutableStateFlow<WorkoutType?>(null)
    val unfinishedPlanWorkoutType = _unfinishedPlanWorkoutType.asStateFlow()

    private var allowedToSaveWorkoutType = false

    private var planBeingCreated: PlanBeingCreated? = null

    init {
        viewModelScope.launch {
            getPlanBeingCreated().also {
                if (it == null) {
                    allowedToSaveWorkoutType = true
                } else {
                    if (isPlanBeingCreatedEmpty(it)) {
                        deletePrevPlan()
                    } else {
                        _unfinishedPlanWorkoutType.value = it.workoutType
                        allowedToSaveWorkoutType = true
                        planBeingCreated = it
                    }
                }
            }
        }
    }

    fun onNavigationBackClick(view: View) {
        view.findNavController().popBackStack()
    }

    fun onWorkoutTypeSelect(view: View, workoutType: WorkoutType) {
        if (!allowedToSaveWorkoutType) {
            return
        }
        viewModelScope.launch {
            saveWorkoutType(workoutType)
            planBeingCreated = PlanBeingCreated(
                id = getPlanBeingCreated.getId()!!,
                name = "",
                planExercises = emptyList(),
                comment = null,
                workoutType = workoutType
            )
            navigateToEditPlanFragment(view)
        }
    }

    private suspend fun saveWorkoutType(workoutType: WorkoutType) {
        savePlan.saveChanges(
            PlanUpdates(null, newWorkoutType = workoutType)
        )
    }

    fun resumePrevPlanCreation(view: View) {
        allowedToSaveWorkoutType = true
        val planHasPlanExercises = planBeingCreated?.planExercises?.isNotEmpty() ?: false
        val unfinishedPlanExercise = planBeingCreated?.planExercises?.find { it.setsNumber == 0 }
        if (planHasPlanExercises && unfinishedPlanExercise != null) {
            navigateToExerciseChoiceFragment(view, unfinishedPlanExercise.ordinal)
        } else {
            navigateToEditPlanFragment(view)
        }
    }

    fun deletePrevPlan() {
        allowedToSaveWorkoutType = true
        viewModelScope.launch {
            deletePlanBeingCreated.invoke()
        }
    }

    private fun navigateToEditPlanFragment(view: View) {
        viewModelScope.launch {
            val action = NewPlanWorkoutTypeFragmentDirections.toPlanEditFragment(
                SetDetailsBuilder.OfPlanEdit(
                    workoutType = planBeingCreated!!.workoutType,
                    planEditType = PlanEditType.Creating,
                    planId = planBeingCreated!!.id
                )
            )
            view.findNavController().navigate(action)
        }
    }

    private fun navigateToExerciseChoiceFragment(view: View, exerciseOrdinal: Int) {
        val action = NewPlanWorkoutTypeFragmentDirections.toExerciseChoiceFragment(
            SetDetailsBuilder.OfPlanEdit(
                workoutType = planBeingCreated!!.workoutType,
                planEditType = PlanEditType.Creating,
                planId = planBeingCreated!!.id,
                exerciseOrdinal = exerciseOrdinal,
            )
        )
        view.findNavController().navigate(action)
    }
}