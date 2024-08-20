package com.training.companion.domain.usecases

import com.training.companion.domain.enums.Language
import com.training.companion.domain.models.PlanBeingCreated
import com.training.companion.domain.repositories.PlanRepository
import com.training.companion.domain.util.getSystemLanguage

class GetPlanBeingCreated(private val planRepository: PlanRepository) {

    suspend operator fun invoke(language: Language = getSystemLanguage()!!): PlanBeingCreated? {
        return planRepository.getPlanBeingCreated(language)
    }

    suspend fun getId(): Int? {
        return planRepository.getPlanBeingCreatedId()
    }
}