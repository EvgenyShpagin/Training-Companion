package com.training.companion.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.companion.data.repositories.ExercisesRepositoryImpl
import com.training.companion.data.repositories.PlanRepositoryImpl
import com.training.companion.domain.usecases.DeletePlanExerciseUseCase
import com.training.companion.domain.usecases.GetIconExerciseList
import com.training.companion.domain.usecases.GetPlanExerciseUseCase
import com.training.companion.domain.usecases.SavePlanUseCase
import com.training.companion.domain.usecases.SearchIconExercise
import com.training.companion.presentation.models.SetDetailsBuilder
import com.training.companion.presentation.viewmodels.ExerciseChoicePlanEditViewModel

class ExerciseChoicePlanEditFactory(
    private val setBuilder: SetDetailsBuilder.OfPlanEdit
) : ViewModelProvider.Factory {

    private val exercisesRepository = ExercisesRepositoryImpl.get()
    private val planRepository = PlanRepositoryImpl.get()

    private val getIconExerciseList = GetIconExerciseList(exercisesRepository)
    private val searchIconExercise = SearchIconExercise(exercisesRepository)
    private val savePlan = SavePlanUseCase(planRepository)
    private val getPlanExercise = GetPlanExerciseUseCase(planRepository)
    private val deletePlanExercise = DeletePlanExerciseUseCase(planRepository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExerciseChoicePlanEditViewModel(
            setBuilder = setBuilder,
            searchIconExercise = searchIconExercise,
            getIconExerciseList = getIconExerciseList,
            savePlanUseCase = savePlan,
            getPlanExercise = getPlanExercise,
            deletePlanExercise = deletePlanExercise,
        ) as T
    }
}