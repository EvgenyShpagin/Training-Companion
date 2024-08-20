package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.PlanRepositoryImpl
import com.training.companion.domain.usecases.DeleteIncompletePlanExercisesUseCase
import com.training.companion.domain.usecases.GetPlanListUseCase
import com.training.companion.presentation.viewmodels.PlansViewModel

class PlansFactory : ViewModelProvider.Factory {

    private val plansRepository = PlanRepositoryImpl.get()

    private val getPlanListUseCase = GetPlanListUseCase(plansRepository)
    private val deleteIncompletePlanExercises =
        DeleteIncompletePlanExercisesUseCase(plansRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlansViewModel(
            getPlanListUseCase,
            deleteIncompletePlanExercises
        ) as T
    }

}