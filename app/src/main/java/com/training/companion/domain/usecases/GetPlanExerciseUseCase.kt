package com.training.companion.domain.usecases

import com.training.companion.domain.models.NullablePlanExercise
import com.training.companion.domain.repositories.PlanRepository
import com.training.companion.domain.util.getSystemLanguage

class GetPlanExerciseUseCase(private val repository: PlanRepository) {

    suspend operator fun invoke(planId: Int, exerciseOrdinal: Int): NullablePlanExercise? {
        return repository.getNullablePlanExercise(planId, exerciseOrdinal, getSystemLanguage()!!)
    }

}