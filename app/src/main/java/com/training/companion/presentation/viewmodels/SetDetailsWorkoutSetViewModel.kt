package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.training.companion.R
import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.CompletedSet
import com.training.companion.domain.models.ExerciseExtras
import com.training.companion.domain.models.Reps
import com.training.companion.domain.models.SetWorkoutSetUseCase
import com.training.companion.domain.models.Time
import com.training.companion.domain.usecases.GetCurrentPlanSetUseCase
import com.training.companion.domain.usecases.GetCurrentSetOrdinalUseCase
import com.training.companion.domain.usecases.GetExerciseExtrasUseCase
import com.training.companion.domain.usecases.GetExerciseUseCase
import com.training.companion.domain.usecases.GetLastExerciseStageInfoUseCase
import com.training.companion.presentation.fragments.SetDetailsFragmentDirections
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.base.SetDetailsViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetDetailsWorkoutSetViewModel(
    setBuilder: SetDetailsBuilder.OfWorkoutSet,
    getExerciseExtrasUseCase: GetExerciseExtrasUseCase,
    private val setCompletedSet: SetWorkoutSetUseCase,
    private val getExercise: GetExerciseUseCase,
    private val getLastStageInfo: GetLastExerciseStageInfoUseCase,
    getCurrentSetOrdinal: GetCurrentSetOrdinalUseCase,
    getPlanSet: GetCurrentPlanSetUseCase
) : SetDetailsViewModel(setBuilder, getExerciseExtrasUseCase) {

    private val workoutSetBuilder = setBuilder
    private val exerciseStageInfo = getLastStageInfo(WorkoutStage.Exercise)!!
    private val setOrdinal = getCurrentSetOrdinal()
    private val planSet = getPlanSet()

    init {
        viewModelScope.launch {
            setupDetails()
            val extras = getExerciseExtras(setBuilder.exerciseId!!)
            setupUI(extras)
        }
    }

    private fun setupDetails() {
        duration = exerciseStageInfo.duration
        restTime = getLastStageInfo(WorkoutStage.Rest)?.duration ?: Time.ZERO

        compareChosenAndSavedExercise(
            ifChosenIsSaved = {
                val savedSetDetails = exerciseStageInfo.workoutSet!!
                reps = savedSetDetails.reps
                weight = savedSetDetails.weight
            },
            ifChosenAndSavedAreDifferent = {
                setCompletedSet(set = null)
            },
            ifChosenNotSaved = {
                planSet?.also { set ->
                    weight = set.weight
                    reps = when (set.reps) {
                        is Reps.Exact -> set.reps
                        else -> null
                    }
                }
            }
        )
    }

    private fun compareChosenAndSavedExercise(
        ifChosenIsSaved: () -> Unit,
        ifChosenAndSavedAreDifferent: () -> Unit,
        ifChosenNotSaved: () -> Unit
    ) {
        if (workoutSetBuilder.exerciseId == exerciseStageInfo.workoutSet?.exercise?.id) {
            ifChosenIsSaved.invoke()
        } else if (exerciseStageInfo.workoutSet != null) {
            ifChosenAndSavedAreDifferent.invoke()
        } else {
            ifChosenNotSaved.invoke()
        }
    }

    override fun onSubmitClick(view: View) {
        viewModelScope.launch {
            val exercise = getExercise(workoutSetBuilder.exerciseId!!)
            setCompletedSet(
                CompletedSet(
                    exercise = exercise,
                    reps = reps as? Reps.Exact,
                    restTime = restTime!!,
                    duration = duration!!,
                    weight = weight,
                    ordinal = setOrdinal
                )
            )
            val action = SetDetailsFragmentDirections.toSetCompletionFragment()
            view.findNavController().navigate(action)
        }
    }

    override fun onBackPressed(view: View) {
        val navController = view.findNavController()
        if (navController.previousBackStackEntry?.destination?.id == R.id.exerciseChoiceFragment) {
            navController.popBackStack()
        } else {
            val action = SetDetailsFragmentDirections.toSetCompletionFragment()
            navController.navigate(action)
        }
    }

    private fun setupUI(extras: ExerciseExtras) {
        _restTimeState.update {
            it.copy(
                isVisible = restTime != null,
                isChosen = restTime != null,
                value = timeToString(restTime),
                onClickListener = null
            )
        }
        _durationState.update {
            it.copy(
                isVisible = true,
                isChosen = true,
                value = timeToString(duration!!),
                onClickListener = null
            )
        }
        extras.apply {
            if (isRepeatable) {
                _repsState.update {
                    it.copy(
                        isVisible = true,
                        isChosen = reps != null,
                        value = reps?.toString(),
                        onClickListener = { view ->
                            showDialog(view, R.id.to_exerciseRepsWorkoutSetBottomSheet)
                        }
                    )
                }
            }
            if (isAdjustableWeight) {
                _weightState.update {
                    it.copy(
                        isVisible = true,
                        isChosen = weight != null,
                        value = weightToString(weight)
                    )
                }
            }
        }
        _allowedToNavigateNext.update { true }
    }
}