package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.PlanRepositoryImpl
import com.training.companion.domain.usecases.DeletePlanBeingCreatedUseCase
import com.training.companion.domain.usecases.GetPlanBeingCreated
import com.training.companion.domain.usecases.IsPlanBeingCreatedEmptyUseCase
import com.training.companion.domain.usecases.SavePlanUseCase
import com.training.companion.presentation.viewmodels.NewPlanTypeViewModel

class WorkoutTypeChoiceFactory : ViewModelProvider.Factory {

    private val repository = PlanRepositoryImpl.get()
    private val savePlanUseCase = SavePlanUseCase(repository)
    private val getPlanBeingCreatedUseCase = GetPlanBeingCreated(repository)
    private val deletePlanBeingCreatedUseCase = DeletePlanBeingCreatedUseCase(repository)
    private val isPlanBeingCreatedEmpty = IsPlanBeingCreatedEmptyUseCase()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewPlanTypeViewModel(
            savePlanUseCase,
            getPlanBeingCreatedUseCase,
            deletePlanBeingCreatedUseCase,
            isPlanBeingCreatedEmpty
        ) as T
    }
}