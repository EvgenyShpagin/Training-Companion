package com.training.companion.domain.usecases

import com.training.companion.domain.enums.Action
import com.training.companion.domain.repositories.ActionsRepository
import com.training.companion.domain.repositories.SessionRepository

class GetAvailableActionsUseCase(
    private val sessionRepository: SessionRepository,
    private val actionsRepository: ActionsRepository
) {
    operator fun invoke(): List<Action> {
        return actionsRepository.getActionsForStage(sessionRepository.getCurrentStage())
    }
}