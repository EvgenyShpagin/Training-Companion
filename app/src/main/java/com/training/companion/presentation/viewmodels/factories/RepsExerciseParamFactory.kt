package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.domain.models.Reps
import com.training.companion.presentation.viewmodels.ExerciseRepsViewModel

class RepsExerciseParamFactory(
    private val previouslyChosenReps: Reps?,
    private val defaultRepsNumber: Reps.Exact
    ): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExerciseRepsViewModel(previouslyChosenReps, defaultRepsNumber) as T
    }
}