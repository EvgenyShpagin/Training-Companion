package com.training.companion.domain.repositories

import com.training.companion.domain.enums.Language
import com.training.companion.domain.models.PastWorkout
import com.training.companion.domain.util.getSystemLanguage


interface WorkoutRepository {
    suspend fun addWorkout(workout: PastWorkout)
    suspend fun getAllWorkouts(language: Language = getSystemLanguage()!!): List<PastWorkout>
}