package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import com.training.companion.data.repositories.PlanRepositoryImpl
import com.training.companion.data.repositories.SessionRepositoryImpl
import com.training.companion.domain.usecases.GetPlanUseCase
import com.training.companion.domain.usecases.GetPlansCountUseCase
import com.training.companion.domain.usecases.SetStageUseCase
import com.training.companion.presentation.viewmodels.ScenarioViewModel

class ScenarioFactory(private val chosenPlanId: Int) : StartingWorkoutFactory() {

    private val planRepository = PlanRepositoryImpl.get()
    private val sessionRepository = SessionRepositoryImpl.get()
    private val getPlansCountUseCase = GetPlansCountUseCase(planRepository)
    private val getPlan = GetPlanUseCase(planRepository)
    private val setStage = SetStageUseCase(sessionRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScenarioViewModel(
            getSessionPrefsUseCase,
            setSessionPrefsUseCase,
            getPlansCountUseCase,
            chosenPlanId,
            getPlan,
            setStage
        ) as T
    }

}