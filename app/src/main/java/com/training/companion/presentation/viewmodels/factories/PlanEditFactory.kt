package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.PlanRepositoryImpl
import com.training.companion.domain.usecases.CheckPlanNameUseCase
import com.training.companion.domain.usecases.DeletePlanUseCase
import com.training.companion.domain.usecases.FormatPlainTextUseCase
import com.training.companion.domain.usecases.GetPlanUseCase
import com.training.companion.domain.usecases.SavePlanUseCase
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.PlanEditViewModel

class PlanEditFactory(
    private val setBuilder: SetDetailsBuilder.OfPlanEdit
) : ViewModelProvider.Factory {

    private val planRepository = PlanRepositoryImpl.get()
    private val savePlanUseCase = SavePlanUseCase(planRepository)
    private val getPlan = GetPlanUseCase(planRepository)
    private val deletePlanUseCase = DeletePlanUseCase(planRepository)
    private val checkPlanName = CheckPlanNameUseCase(planRepository)
    private val formatPlainText = FormatPlainTextUseCase()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlanEditViewModel(
            setBuilder,
            savePlanUseCase,
            getPlan,
            deletePlanUseCase,
            checkPlanName,
            formatPlainText
        ) as T
    }
}