package com.training.companion.presentation.viewmodels.base

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.training.companion.domain.models.ExerciseListFilter
import com.training.companion.domain.models.IconExercise
import com.training.companion.domain.usecases.GetIconExerciseList
import com.training.companion.domain.usecases.SearchIconExercise
import com.training.companion.presentation.fragments.ExerciseChoiceFragmentDirections
import com.training.companion.presentation.models.SetDetailsBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class ExerciseChoiceViewModel(
    protected var setBuilder: SetDetailsBuilder,
    private val searchIconExercise: SearchIconExercise,
    private val getIconExerciseList: GetIconExerciseList,
) : ViewModel() {

    var selectedExercise: IconExercise? = null
        protected set(value) {
            field = value
            _exerciseIsSelected.update { field != null }
        }

    protected var currentListFilter: ExerciseListFilter? = null

    private val _exerciseIsSelected = MutableStateFlow(false)
    val exerciseIsSelected = _exerciseIsSelected.asStateFlow()

    protected val _chosenExerciseIdOnLaunch = MutableStateFlow(selectedExercise?.id)
    val chosenExerciseIdOnLaunch = _chosenExerciseIdOnLaunch.asStateFlow()


    suspend fun getExercisesWithTopChosen(): List<IconExercise> {
        while (currentListFilter == null) {
            delay(20)
        }
        return getIconExerciseList(currentListFilter!!, selectedExercise?.id)
    }

    suspend fun searchForIconExercise(exerciseName: String): List<IconExercise> {
        return searchIconExercise.invoke(exerciseName, currentListFilter!!)
    }

    abstract fun onBackPressed(view: View)

    fun onExerciseClicked(exercise: IconExercise) {
        selectedExercise = exercise
    }

    open fun navigateToDetails(view: View) {
        val action = ExerciseChoiceFragmentDirections.toExerciseDetailsFragment(setBuilder)
        view.findNavController().navigate(action)
    }

    open fun navigateToFilterFragment(view: View) {
        val updatedSetBuilder = when (val setBuilder = setBuilder) {
            is SetDetailsBuilder.OfWorkoutSet ->
                setBuilder.copy(equipment = currentListFilter!!.byEquipmentCode)

            is SetDetailsBuilder.OfPlanEdit ->
                setBuilder.copy(equipment = currentListFilter!!.byEquipmentCode)
        }
        val action = ExerciseChoiceFragmentDirections.toEquipmentFilterFragment(updatedSetBuilder)
        view.findNavController().navigate(action)
    }
}