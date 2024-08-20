package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.PlanRepositoryImpl
import com.training.companion.data.repositories.SessionRepositoryImpl
import com.training.companion.domain.usecases.DeletePlanUseCase
import com.training.companion.domain.usecases.DuplicatePlanUseCase
import com.training.companion.domain.usecases.GetPlanUseCase
import com.training.companion.domain.usecases.SetSessionPrefsUseCase
import com.training.companion.domain.usecases.SetStageUseCase
import com.training.companion.presentation.viewmodels.PlanDetailsViewModel

class PlanDetailsFactory(private val planId: Int) : ViewModelProvider.Factory {

    private val planRepository = PlanRepositoryImpl.get()
    private val sessionRepository = SessionRepositoryImpl.get()
    private val getPlan = GetPlanUseCase(planRepository)
    private val deletePlan = DeletePlanUseCase(planRepository)
    private val duplicatePlan = DuplicatePlanUseCase(planRepository)
    private val setStage = SetStageUseCase(sessionRepository)
    private val setSessionPrefs = SetSessionPrefsUseCase(sessionRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlanDetailsViewModel(
            planId,
            getPlan,
            duplicatePlan,
            deletePlan,
            setStage,
            setSessionPrefs
        ) as T
    }

}