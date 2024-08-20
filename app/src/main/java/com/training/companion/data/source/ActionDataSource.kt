package com.training.companion.data.source

import com.training.companion.domain.enums.Action

class ActionDataSource {

    private companion object {
        val EXERCISE_ACTIONS = listOf(
            Action.RESTART_SET,
            Action.GO_TO_REST,
            //Action.ADD_EXTRA_STOPWATCH
        )
        val REST_ACTIONS = listOf(
            Action.START_EXERCISE,
            Action.CHANGE_REST_TIME,
            Action.RETURN_TO_PREVIOUS_STAGE,
            //Action.ADD_EXTRA_STOPWATCH
        )
        val WARMUP_ACTIONS = listOf(
            Action.GO_TO_REST,
            Action.START_EXERCISE,
            //Action.ADD_EXTRA_STOPWATCH
        )
        val STRETCHING_ACTIONS = listOf(Action.ADD_EXTRA_STOPWATCH)
    }

    fun getExerciseStageActions() = EXERCISE_ACTIONS
    fun getRestStageActions() = REST_ACTIONS
    fun getWarmUpStageActions() = WARMUP_ACTIONS
    fun getStretchingStageActions() = STRETCHING_ACTIONS
}