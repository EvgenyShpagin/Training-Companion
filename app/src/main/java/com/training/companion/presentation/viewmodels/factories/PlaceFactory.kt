package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.training.companion.presentation.viewmodels.PlaceViewModel

class PlaceFactory : StartingWorkoutFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlaceViewModel(
            getSessionPrefsUseCase,
            setSessionPrefsUseCase
        ) as T
    }

}