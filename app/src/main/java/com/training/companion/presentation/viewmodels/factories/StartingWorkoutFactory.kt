package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.SessionRepositoryImpl
import com.training.companion.domain.usecases.SetSessionPrefsUseCase
import com.training.companion.domain.usecases.GetSessionPrefsUseCase

abstract class StartingWorkoutFactory : ViewModelProvider.Factory {

    private val sessionRepository = SessionRepositoryImpl.get()

    protected val getSessionPrefsUseCase = GetSessionPrefsUseCase(sessionRepository)
    protected val setSessionPrefsUseCase = SetSessionPrefsUseCase(sessionRepository)
}