package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.SessionRepositoryImpl
import com.training.companion.domain.usecases.GetCurrentPlanSetUseCase
import com.training.companion.domain.usecases.GetLastExerciseStageInfoUseCase
import com.training.companion.domain.usecases.GetWorkoutTypeUseCase
import com.training.companion.presentation.viewmodels.SetCompletionViewModel

class SetCompletionFactory: ViewModelProvider.Factory {

    private val sessionRepository = SessionRepositoryImpl.get()
    private val getLastExerciseStageInfo = GetLastExerciseStageInfoUseCase(sessionRepository)
    private val getWorkoutType = GetWorkoutTypeUseCase(sessionRepository)
    private val getCurrentPlanSet = GetCurrentPlanSetUseCase(sessionRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SetCompletionViewModel(
            getWorkoutType,
            getLastExerciseStageInfo,
            getCurrentPlanSet
        ) as T
    }
}