package com.training.companion.domain.models

import com.training.companion.domain.enums.WorkoutStage

data class PassedStageInfo(
    val stage: WorkoutStage,
    val duration: Time,
    val workoutSet: CompletedSet?
)