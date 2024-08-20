package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.training.companion.presentation.viewmodels.TrainingTypeViewModel

class TrainingTypeFactory : StartingWorkoutFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TrainingTypeViewModel(
            getSessionPrefsUseCase,
            setSessionPrefsUseCase
        ) as T
    }

}