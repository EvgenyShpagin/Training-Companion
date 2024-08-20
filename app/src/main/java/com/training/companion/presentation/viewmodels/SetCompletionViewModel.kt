package com.training.companion.presentation.viewmodels

import android.view.View
import android.widget.Toast
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import com.training.companion.R
import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.Time
import com.training.companion.domain.usecases.GetCurrentPlanSetUseCase
import com.training.companion.domain.usecases.GetLastExerciseStageInfoUseCase
import com.training.companion.domain.usecases.GetWorkoutTypeUseCase
import com.training.companion.presentation.dialogs.SetCompletionBottomSheetDirections
import com.training.companion.presentation.models.SetDetailsBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SetCompletionViewModel(
    getWorkoutType: GetWorkoutTypeUseCase,
    getLastExerciseStageInfo: GetLastExerciseStageInfoUseCase,
    getPlanSet: GetCurrentPlanSetUseCase
) : ViewModel() {

    private val workoutType = getWorkoutType()

    private val exerciseStageInfo = getLastExerciseStageInfo(WorkoutStage.Exercise)!!
    private val completedSet = exerciseStageInfo.workoutSet
    private val planSet = getPlanSet()

    private val exercise get() = completedSet?.exercise ?: planSet?.exercise

    private val _uiState = MutableStateFlow(
        UiState(
            setDuration = exerciseStageInfo.duration,
            detailsAreChosen = completedSet?.duration != null,
            chosenExerciseName = exercise?.name,
            exerciseIsChosen = exercise != null
        )
    )
    val uiState = _uiState.asStateFlow()


    fun onBackPress(view: View) {
        Toast.makeText(view.context, R.string.toast_enter_set_details, Toast.LENGTH_SHORT).show()
    }

    fun onExerciseClick(view: View) {
        val action = SetCompletionBottomSheetDirections.toExerciseChoiceFragment(
            setBuilder = SetDetailsBuilder.OfWorkoutSet(
                workoutType = workoutType,
                exerciseId = exercise?.id
            )
        )
        getNavController(view).navigate(action)
    }

    fun onDetailsClick(view: View) {
        val action = SetCompletionBottomSheetDirections.toExerciseDetailsFragment(
            setBuilder = SetDetailsBuilder.OfWorkoutSet(
                workoutType = workoutType,
                exerciseId = exercise?.id
            )
        )
        getNavController(view).navigate(action)
    }

    fun onContinueClick(view: View) {
        getNavController(view).popBackStack()
    }

    private fun getNavController(view: View) =
        NavHostFragment.findNavController(view.findFragment())

    data class UiState(
        val setDuration: Time = Time.ZERO,
        val chosenExerciseName: String? = null,
        val exerciseIsChosen: Boolean = false,
        val detailsAreChosen: Boolean = false,
    )
}