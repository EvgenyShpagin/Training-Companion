package com.training.companion.domain.models

data class Set(
    val exercise: IconExercise?,
    val reps: Reps?,
    val restTime: Time?,
    val duration: Time?,
    val weight: Weight?,
)