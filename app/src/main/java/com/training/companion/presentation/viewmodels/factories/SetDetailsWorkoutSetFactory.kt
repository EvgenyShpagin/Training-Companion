package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.ExercisesRepositoryImpl
import com.training.companion.data.repositories.SessionRepositoryImpl
import com.training.companion.domain.models.SetWorkoutSetUseCase
import com.training.companion.domain.usecases.GetCurrentPlanSetUseCase
import com.training.companion.domain.usecases.GetCurrentSetOrdinalUseCase
import com.training.companion.domain.usecases.GetExerciseExtrasUseCase
import com.training.companion.domain.usecases.GetExerciseUseCase
import com.training.companion.domain.usecases.GetLastExerciseStageInfoUseCase
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.SetDetailsWorkoutSetViewModel

class SetDetailsWorkoutSetFactory(
    private val setBuilder: SetDetailsBuilder.OfWorkoutSet,
) : ViewModelProvider.Factory {

    private val exerciseRepository = ExercisesRepositoryImpl.get()
    private val sessionRepository = SessionRepositoryImpl.get()
    private val getExerciseExtrasUseCase = GetExerciseExtrasUseCase(exerciseRepository)
    private val setWorkoutSet = SetWorkoutSetUseCase(sessionRepository)
    private val getExercise = GetExerciseUseCase(exerciseRepository)
    private val getLastPassedStageInfo = GetLastExerciseStageInfoUseCase(sessionRepository)
    private val getLastSetOrdinal = GetCurrentSetOrdinalUseCase(sessionRepository)
    private val getCurrentPlanSet = GetCurrentPlanSetUseCase(sessionRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SetDetailsWorkoutSetViewModel(
            setBuilder,
            getExerciseExtrasUseCase,
            setWorkoutSet,
            getExercise,
            getLastPassedStageInfo,
            getLastSetOrdinal,
            getCurrentPlanSet
        ) as T
    }

}