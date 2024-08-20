package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.SessionRepositoryImpl
import com.training.companion.domain.usecases.GetStageTimeUseCase
import com.training.companion.domain.usecases.SetPreviousStageUseCase
import com.training.companion.presentation.viewmodels.SuspenseViewModel

class SuspenseFactory : ViewModelProvider.Factory {

    private val sessionRepository = SessionRepositoryImpl.get()
    private val getStageTimeUseCase = GetStageTimeUseCase(sessionRepository)
    private val setPreviousStageUseCase = SetPreviousStageUseCase(sessionRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SuspenseViewModel(getStageTimeUseCase, setPreviousStageUseCase) as T
    }
}