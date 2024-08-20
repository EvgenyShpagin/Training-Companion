package com.training.companion.domain.models

data class CompletedSet(
    val ordinal: Int,
    val exercise: IconExercise,
    val restTime: Time,
    val reps: Reps.Exact?,
    val duration: Time,
    val weight: Weight?,
)