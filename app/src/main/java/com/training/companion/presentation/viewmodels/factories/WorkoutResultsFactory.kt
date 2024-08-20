package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.SessionRepositoryImpl
import com.training.companion.domain.usecases.FinishSessionUseCase
import com.training.companion.domain.usecases.GetCompletedSetsUseCase
import com.training.companion.presentation.viewmodels.WorkoutResultsViewModel

class WorkoutResultsFactory : ViewModelProvider.Factory {

    private val sessionRepository = SessionRepositoryImpl.get()
    private val clearSession = FinishSessionUseCase(sessionRepository)
    private val getCompletedSets = GetCompletedSetsUseCase(sessionRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorkoutResultsViewModel(clearSession, getCompletedSets) as T
    }

}