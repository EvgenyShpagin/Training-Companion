package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.domain.models.Weight
import com.training.companion.presentation.viewmodels.WeightExerciseViewModel

class WeightExerciseParamFactory(
    private val previouslyChosenWeight: Weight?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeightExerciseViewModel(previouslyChosenWeight) as T
    }

}