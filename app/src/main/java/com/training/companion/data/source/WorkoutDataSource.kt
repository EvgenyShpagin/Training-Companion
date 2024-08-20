package com.training.companion.data.source

import com.training.companion.data.models.intermediate.PastWorkoutWithSets
import com.training.companion.data.room.dao.WorkoutDao

class WorkoutDataSource(private val dao: WorkoutDao) {

    suspend fun getPastWorkouts(): List<PastWorkoutWithSets> {
        return dao.getWorkouts()
    }
}