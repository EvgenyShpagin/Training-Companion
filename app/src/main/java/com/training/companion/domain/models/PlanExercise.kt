package com.training.companion.domain.models


data class PlanExercise(
    val id: Int,
    val ordinal: Int,
    val setsNumber: Int,
    val set: Set
)