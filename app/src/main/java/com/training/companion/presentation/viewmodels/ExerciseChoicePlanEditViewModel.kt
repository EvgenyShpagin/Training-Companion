package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.training.companion.domain.models.ExerciseListFilter
import com.training.companion.domain.models.NullablePlanExercise
import com.training.companion.domain.models.PlanUpdates
import com.training.companion.domain.models.Set
import com.training.companion.domain.usecases.DeletePlanExerciseUseCase
import com.training.companion.domain.usecases.GetIconExerciseList
import com.training.companion.domain.usecases.GetPlanExerciseUseCase
import com.training.companion.domain.usecases.SavePlanUseCase
import com.training.companion.domain.usecases.SearchIconExercise
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.base.ExerciseChoiceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExerciseChoicePlanEditViewModel(
    setBuilder: SetDetailsBuilder.OfPlanEdit,
    searchIconExercise: SearchIconExercise,
    getIconExerciseList: GetIconExerciseList,
    private val getPlanExercise: GetPlanExerciseUseCase,
    private val savePlanUseCase: SavePlanUseCase,
    private val deletePlanExercise: DeletePlanExerciseUseCase,
) : ExerciseChoiceViewModel(setBuilder, searchIconExercise, getIconExerciseList) {

    private val setBuilderOfPlanEdit = setBuilder
    private var storagePlanExercise: NullablePlanExercise? = null

    init {
        viewModelScope.launch {
            storagePlanExercise = getPlanExercise(setBuilder.planId, setBuilder.exerciseOrdinal!!)
            selectedExercise = storagePlanExercise?.set?.exercise
            _chosenExerciseIdOnLaunch.update { selectedExercise?.id }

            currentListFilter = ExerciseListFilter(
                byWorkoutType = setBuilder.workoutType,
                byEquipmentCode = setBuilder.equipment
            )
        }
    }

    override fun onBackPressed(view: View) {
        viewModelScope.launch(Dispatchers.Main) {
            storagePlanExercise?.let {
                if (!it.isCorrect) {
                    deletePlanExercise(it.id!!)
                }
            }
            view.findNavController().popBackStack()
        }
    }

    override fun navigateToDetails(view: View) {
        saveChosenExercise()
        super.navigateToDetails(view)
    }

    override fun navigateToFilterFragment(view: View) {
        if (exerciseIsSelected.value) {
            saveChosenExercise()
        }
        super.navigateToFilterFragment(view)
    }

    private fun saveChosenExercise() {
        val planExercise = formPlanExercise()
        viewModelScope.launch {
            when {
                storagePlanExercise == null -> addNewPlanExercise(planExercise)
                wasExerciseReplaced -> updateExistingPlanExercise(planExercise)
            }
        }
    }

    private suspend fun addNewPlanExercise(planExercise: NullablePlanExercise) {
        PlanUpdates(planId = setBuilderOfPlanEdit.planId, newPlanExercise = planExercise).also {
            savePlanUseCase.saveChanges(it)
        }
    }

    private suspend fun updateExistingPlanExercise(planExercise: NullablePlanExercise) {
        val updatedPlanExercise = PlanUpdates(
            planId = setBuilderOfPlanEdit.planId,
            updatedPlanExercise = planExercise
        )
        savePlanUseCase.saveChanges(updatedPlanExercise)
    }

    private fun formPlanExercise(): NullablePlanExercise {
        return NullablePlanExercise(
            ordinal = setBuilderOfPlanEdit.exerciseOrdinal!!,
            set = Set(
                exercise = selectedExercise!!,
                reps = null,
                restTime = null,
                duration = null,
                weight = null,
            ),
            setsNumber = 0,
            id = storagePlanExercise?.id
        )
    }

    private val wasExerciseReplaced: Boolean
        get() = selectedExercise!! != storagePlanExercise?.set?.exercise
}