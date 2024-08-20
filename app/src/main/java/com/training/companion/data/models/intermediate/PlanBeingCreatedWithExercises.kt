package com.training.companion.data.models.intermediate

data class PlanBeingCreatedWithExercises(
    val id: Int,
    val name: String,
    val comment: String?,
    val planExercises: List<ExerciseSet>,
    val workoutTypeId: Int,
)
