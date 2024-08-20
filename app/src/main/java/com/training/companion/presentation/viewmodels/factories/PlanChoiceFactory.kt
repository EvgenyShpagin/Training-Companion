package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.PlanRepositoryImpl
import com.training.companion.domain.usecases.GetPlanListUseCase
import com.training.companion.presentation.viewmodels.PlanChoiceViewModel

class PlanChoiceFactory : ViewModelProvider.Factory {

    private val plansRepository = PlanRepositoryImpl.get()

    private val getPlanListUseCase = GetPlanListUseCase(plansRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlanChoiceViewModel(getPlanListUseCase) as T
    }

}