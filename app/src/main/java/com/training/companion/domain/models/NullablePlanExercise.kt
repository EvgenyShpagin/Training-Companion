package com.training.companion.domain.models

data class NullablePlanExercise(
    val ordinal: Int,
    val setsNumber: Int?,
    val set: Set,
    val id: Int? = null
) {
    val isCorrect get() = setsNumber != null
}