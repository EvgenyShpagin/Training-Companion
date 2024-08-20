package com.training.companion.data.repositories

import com.training.companion.data.source.ActionDataSource
import com.training.companion.domain.enums.Action
import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.repositories.ActionsRepository

class ActionsRepositoryImpl private constructor(
    private val actionsSource: ActionDataSource
) : ActionsRepository {

    override fun getActionsForStage(stage: WorkoutStage): List<Action> {
        return when (stage) {
            WorkoutStage.WarmUp -> actionsSource.getWarmUpStageActions()
            WorkoutStage.Exercise -> actionsSource.getExerciseStageActions()
            WorkoutStage.Rest -> actionsSource.getRestStageActions()
            WorkoutStage.Stretching -> actionsSource.getStretchingStageActions()
            else -> emptyList()
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: ActionsRepositoryImpl? = null

        fun initialize(dataSource: ActionDataSource) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = ActionsRepositoryImpl(dataSource)
                }
            }
        }

        fun get(): ActionsRepository {
            return INSTANCE ?: throw IllegalStateException("ActionRepository must be initialized")
        }
    }
}