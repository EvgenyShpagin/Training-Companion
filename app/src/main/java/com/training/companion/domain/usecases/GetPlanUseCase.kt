package com.training.companion.domain.usecases

import com.training.companion.domain.enums.Language
import com.training.companion.domain.models.WorkoutPlan
import com.training.companion.domain.repositories.PlanRepository
import com.training.companion.domain.util.getSystemLanguage

class GetPlanUseCase(private val planRepository: PlanRepository) {
    suspend operator fun invoke(planId: Int, language: Language = getSystemLanguage()!!): WorkoutPlan {
        return planRepository.getPlan(planId, language)
    }
}