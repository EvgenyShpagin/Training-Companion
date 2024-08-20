package com.training.companion.data.models

import com.training.companion.domain.enums.WorkoutPlace
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.Time
import com.training.companion.domain.models.WorkoutPlan

sealed class SessionPrefs {
    data class WithPlan(val plan: WorkoutPlan) : SessionPrefs()
    data class WithoutPlan(
        val restTime: Time?,
        val type: WorkoutType,
        val place: WorkoutPlace,
    ) : SessionPrefs()
}