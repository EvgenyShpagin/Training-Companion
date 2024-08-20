package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.domain.models.Time
import com.training.companion.domain.usecases.CheckTimeIsCorrectUseCase
import com.training.companion.presentation.viewmodels.base.TimeRelatedExerciseParamViewModel

class TimeRelatedExerciseParamFactory(
    private val previouslyChosenTime: Time?, private val defaultTime: Time
) : ViewModelProvider.Factory {

    private val timeChecker = CheckTimeIsCorrectUseCase()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TimeRelatedExerciseParamViewModel(
            previouslyChosenTime,
            timeChecker,
            defaultTime
        ) as T
    }

}