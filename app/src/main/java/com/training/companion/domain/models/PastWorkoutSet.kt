package com.training.companion.domain.models

@Deprecated("Old implementation")
data class PastWorkoutSet(
    val setOrder: Int,
    val reps: Reps?,
    val restSeconds: Int?,
    val durationSeconds: Int?,
    val exercise: Exercise
)