package com.training.companion.domain.repositories

import com.training.companion.domain.enums.Action
import com.training.companion.domain.enums.WorkoutStage

interface ActionsRepository {
    fun getActionsForStage(stage: WorkoutStage): List<Action>
}