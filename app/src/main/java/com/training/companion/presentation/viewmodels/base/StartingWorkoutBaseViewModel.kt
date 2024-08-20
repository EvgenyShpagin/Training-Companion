package com.training.companion.presentation.viewmodels.base

import com.training.companion.domain.usecases.GetSessionPrefsUseCase
import com.training.companion.domain.usecases.SetSessionPrefsUseCase

abstract class StartingWorkoutBaseViewModel(
    protected val getSessionPrefsUseCase: GetSessionPrefsUseCase,
    protected val setSessionPrefsUseCase: SetSessionPrefsUseCase
) : ObservableViewModel()