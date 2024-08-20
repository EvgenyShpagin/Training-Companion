package com.training.companion.domain.models

@Deprecated("Old implementation")
data class WorkoutSet(
    val exercise: Exercise,
    var reps: Int?,
    var timeToExercise: Time?
)