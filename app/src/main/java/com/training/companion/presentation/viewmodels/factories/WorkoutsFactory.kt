package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.WorkoutRepositoryImpl
import com.training.companion.domain.usecases.GetWorkoutsUseCase
import com.training.companion.presentation.viewmodels.WorkoutsViewModel

class WorkoutsFactory : ViewModelProvider.Factory {

    private val workoutRepository = WorkoutRepositoryImpl.get()
    private val getWorkoutsUseCase = GetWorkoutsUseCase(workoutRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorkoutsViewModel(getWorkoutsUseCase) as T
    }
}
