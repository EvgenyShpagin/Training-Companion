package com.training.companion.domain.usecases

import com.training.companion.domain.enums.Language
import com.training.companion.domain.models.WorkoutPlan
import com.training.companion.domain.repositories.PlanRepository
import com.training.companion.domain.util.getSystemLanguage

class GetPlanListUseCase(private val plansRepository: PlanRepository) {
    suspend operator fun invoke(language: Language = getSystemLanguage()!!): List<WorkoutPlan> {
        return plansRepository.getPlans(language)
    }
}