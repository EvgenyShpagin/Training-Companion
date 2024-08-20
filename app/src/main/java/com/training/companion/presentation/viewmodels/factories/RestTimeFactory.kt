package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.training.companion.data.repositories.SessionRepositoryImpl
import com.training.companion.domain.usecases.SetStagePreferencesUseCase
import com.training.companion.domain.usecases.SetStageUseCase
import com.training.companion.presentation.viewmodels.RestTimeViewModel

class RestTimeFactory : StartingWorkoutFactory() {

    private val sessionRepository = SessionRepositoryImpl.get()
    private val setStagePreferencesUseCase = SetStagePreferencesUseCase(sessionRepository)
    private val setStageUseCase = SetStageUseCase(sessionRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RestTimeViewModel(
            setStagePreferencesUseCase,
            getSessionPrefsUseCase,
            setSessionPrefsUseCase,
            setStageUseCase
        ) as T
    }

}