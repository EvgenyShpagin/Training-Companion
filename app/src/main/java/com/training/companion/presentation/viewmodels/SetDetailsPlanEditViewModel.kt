package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.training.companion.domain.models.ExerciseExtras
import com.training.companion.domain.models.NullablePlanExercise
import com.training.companion.domain.models.PlanUpdates
import com.training.companion.domain.models.Set
import com.training.companion.domain.models.Time
import com.training.companion.domain.usecases.GetExerciseExtrasUseCase
import com.training.companion.domain.usecases.GetPlanExerciseUseCase
import com.training.companion.domain.usecases.SavePlanUseCase
import com.training.companion.presentation.fragments.SetDetailsFragmentDirections
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.base.SetDetailsViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetDetailsPlanEditViewModel(
    setBuilder: SetDetailsBuilder.OfPlanEdit,
    private val getPlanExercise: GetPlanExerciseUseCase,
    getExerciseExtrasUseCase: GetExerciseExtrasUseCase,
    private val savePlanUseCase: SavePlanUseCase,
) : SetDetailsViewModel(setBuilder, getExerciseExtrasUseCase) {

    private var planExercise: NullablePlanExercise? = null

    private val planEditSetBuilder = setBuilder

    init {
        viewModelScope.launch {
            getPlanExercise(setBuilder.planId, setBuilder.exerciseOrdinal!!)!!.also { planExercise ->
                this@SetDetailsPlanEditViewModel.planExercise = planExercise
                applyValues(planExercise)
                val extras = getExerciseExtras(planExercise.set.exercise!!.id)
                setupUI(extras)
            }
        }
    }

    override fun onSetsNumberChosen(count: Int) {
        if (setsNumber == null) {
            _allowedToNavigateNext.update { true }
        }
        super.onSetsNumberChosen(count)
    }

    override fun onSubmitClick(view: View) {
        viewModelScope.launch {
            val updatedPlanExercise = NullablePlanExercise(
                id = planExercise!!.id,
                ordinal = planExercise!!.ordinal,
                setsNumber = setsNumber,
                set = Set(
                    exercise = planExercise!!.set.exercise,
                    reps = reps,
                    restTime = restTime,
                    duration = duration,
                    weight = weight
                )
            )
            savePlanUseCase.saveChanges(
                PlanUpdates(planId = planEditSetBuilder.planId, updatedPlanExercise = updatedPlanExercise)
            )
        }
        val action = SetDetailsFragmentDirections.backToPlanEditFragment(setBuilder)
        view.findNavController().navigate(action)
    }

    private fun applyValues(exercise: NullablePlanExercise) {
        setsNumber = exercise.setsNumber
        reps = exercise.set.reps
        restTime = exercise.set.restTime
        duration = exercise.set.duration
        weight = exercise.set.weight?.convert(requiredWeightUnits)
    }

    private fun setupUI(extras: ExerciseExtras) {
        _setState.update {
            it.copy(
                isVisible = true,
                isChosen = setsNumber != null,
                value = setsNumber?.toString()
            )
        }
        _restTimeState.update {
            it.copy(
                isVisible = true,
                isChosen = restTime != null,
                value = restTime?.toString(Time.Format.MM_SS)
            )
        }
        _durationState.update {
            it.copy(
                isVisible = true,
                isChosen = duration != null,
                value = duration?.toString(Time.Format.MM_SS)
            )
        }
        if (extras.isRepeatable) {
            _repsState.update {
                it.copy(
                    isVisible = true,
                    isChosen = reps != null,
                    value = reps?.toString()
                )
            }
        }
        if (extras.isAdjustableWeight) {
            _weightState.update {
                it.copy(
                    isVisible = true,
                    isChosen = weight != null,
                    value = weight?.toString()
                )
            }
        }
        if (setsNumber != null) {
            _allowedToNavigateNext.update { true }
        }
    }
}