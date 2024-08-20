package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.ActionsRepositoryImpl
import com.training.companion.data.repositories.SessionRepositoryImpl
import com.training.companion.domain.usecases.GetAvailableActionsUseCase
import com.training.companion.domain.usecases.GetCompletedSetsUseCase
import com.training.companion.domain.usecases.GetCurrentPlanSetUseCase
import com.training.companion.domain.usecases.GetCurrentStageUseCase
import com.training.companion.domain.usecases.GetLastCompletedSetUseCase
import com.training.companion.domain.usecases.GetNextSetUseCase
import com.training.companion.domain.usecases.GetStagePreferencesUseCase
import com.training.companion.domain.usecases.GetStageTimeUseCase
import com.training.companion.domain.usecases.GetStageUseCase
import com.training.companion.domain.usecases.GetTotalTimeUseCase
import com.training.companion.domain.usecases.IsSetCommitNotFinished
import com.training.companion.domain.usecases.IsWorkoutInProgress
import com.training.companion.domain.usecases.RestartStageUseCase
import com.training.companion.domain.usecases.SetStageUseCase
import com.training.companion.presentation.viewmodels.WorkoutViewModel

class WorkoutFactory : ViewModelProvider.Factory {

    private val sessionRepository = SessionRepositoryImpl.get()
    private val actionsRepository = ActionsRepositoryImpl.get()

    private val nextSet = GetNextSetUseCase(sessionRepository)
    private val getAvailableActions =
        GetAvailableActionsUseCase(sessionRepository, actionsRepository)
    private val getLastCompletedSets = GetCompletedSetsUseCase(sessionRepository)
    private val getLastCompletedSet = GetLastCompletedSetUseCase(sessionRepository)
    private val setStage = SetStageUseCase(sessionRepository)
    private val getTotalTime = GetTotalTimeUseCase(sessionRepository)
    private val getStagePreferences = GetStagePreferencesUseCase(sessionRepository)
    private val getStageTime = GetStageTimeUseCase(sessionRepository)
    private val getStage = GetStageUseCase(sessionRepository)
    private val isSetCommitNotFinished = IsSetCommitNotFinished(sessionRepository)
    private val getCurrentPlanSet = GetCurrentPlanSetUseCase(sessionRepository)
    private val isWorkoutInProgress = IsWorkoutInProgress(sessionRepository)
    private val getCurrentStage = GetCurrentStageUseCase(sessionRepository)
    private val restartStage = RestartStageUseCase(sessionRepository)


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorkoutViewModel(
            setStage = setStage,
            getTotalTimeUseCase = getTotalTime,
            getStageTimeUseCase = getStageTime,
            getStageUseCase = getStage,
            getStagePreferencesUseCase = getStagePreferences,
            isSetCommitNotFinished = isSetCommitNotFinished,
            getNextPlanSet = nextSet,
            getAvailableActions = getAvailableActions,
            getCompletedSets = getLastCompletedSets,
            getLastCompletedSet = getLastCompletedSet,
            getCurrentPlanSet = getCurrentPlanSet,
            isWorkoutInProgress = isWorkoutInProgress,
            restartStage = restartStage,
            getCurrentStage = getCurrentStage
        ) as T
    }
}