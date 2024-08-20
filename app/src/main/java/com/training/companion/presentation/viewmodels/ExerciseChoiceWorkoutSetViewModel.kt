package com.training.companion.presentation.viewmodels

import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.training.companion.domain.models.ExerciseListFilter
import com.training.companion.domain.usecases.GetExerciseUseCase
import com.training.companion.domain.usecases.GetIconExerciseList
import com.training.companion.domain.usecases.SearchIconExercise
import com.training.companion.presentation.fragments.ExerciseChoiceFragmentDirections
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.base.ExerciseChoiceViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExerciseChoiceWorkoutSetViewModel(
    setBuilder: SetDetailsBuilder.OfWorkoutSet,
    searchIconExercise: SearchIconExercise,
    getIconExerciseList: GetIconExerciseList,
    private val getExercise: GetExerciseUseCase,
) : ExerciseChoiceViewModel(setBuilder, searchIconExercise, getIconExerciseList) {

    init {
        viewModelScope.launch {
            currentListFilter = ExerciseListFilter(setBuilder.workoutType, setBuilder.equipment)
            selectedExercise = setBuilder.exerciseId?.let { getExercise(it) }
            _chosenExerciseIdOnLaunch.update { selectedExercise?.id }
        }
    }

    override fun navigateToDetails(view: View) {
        val setBuilderOfWorkoutSet = (setBuilder as SetDetailsBuilder.OfWorkoutSet)
        setBuilder = setBuilderOfWorkoutSet.copy(exerciseId = selectedExercise!!.id)
        super.navigateToDetails(view)
    }

    override fun onBackPressed(view: View) {
        val action = ExerciseChoiceFragmentDirections.toSetCompletionFragment()
        view.findNavController().navigate(action)
    }
}