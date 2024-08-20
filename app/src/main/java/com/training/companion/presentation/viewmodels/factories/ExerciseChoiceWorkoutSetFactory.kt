package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.ExercisesRepositoryImpl
import com.training.companion.domain.usecases.GetExerciseUseCase
import com.training.companion.domain.usecases.GetIconExerciseList
import com.training.companion.domain.usecases.SearchIconExercise
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.ExerciseChoiceWorkoutSetViewModel

class ExerciseChoiceWorkoutSetFactory(
    private val setBuilder: SetDetailsBuilder.OfWorkoutSet
) : ViewModelProvider.Factory {

    private val exerciseRepository = ExercisesRepositoryImpl.get()

    private val getIconExerciseList = GetIconExerciseList(exerciseRepository)
    private val searchIconExercise = SearchIconExercise(exerciseRepository)
    private val getExercise = GetExerciseUseCase(exerciseRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExerciseChoiceWorkoutSetViewModel(
            setBuilder = setBuilder,
            searchIconExercise = searchIconExercise,
            getIconExerciseList = getIconExerciseList,
            getExercise = getExercise,
        ) as T
    }
}