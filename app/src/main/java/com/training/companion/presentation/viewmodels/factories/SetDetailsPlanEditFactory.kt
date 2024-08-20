package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.ExercisesRepositoryImpl
import com.training.companion.data.repositories.PlanRepositoryImpl
import com.training.companion.domain.usecases.GetExerciseExtrasUseCase
import com.training.companion.domain.usecases.GetPlanExerciseUseCase
import com.training.companion.domain.usecases.SavePlanUseCase
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.SetDetailsPlanEditViewModel

class SetDetailsPlanEditFactory(
    private val setBuilder: SetDetailsBuilder.OfPlanEdit
) : ViewModelProvider.Factory {

    private val exerciseRep = ExercisesRepositoryImpl.get()
    private val planRep = PlanRepositoryImpl.get()
    private val getExerciseExtrasUseCase = GetExerciseExtrasUseCase(exerciseRep)
    private val getExerciseOfPlanBeingCreated = GetPlanExerciseUseCase(planRep)
    private val savePlanUseCase = SavePlanUseCase(planRep)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SetDetailsPlanEditViewModel(
            setBuilder,
            getExerciseOfPlanBeingCreated,
            getExerciseExtrasUseCase,
            savePlanUseCase
        ) as T
    }

}