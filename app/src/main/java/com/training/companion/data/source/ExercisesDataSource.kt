package com.training.companion.data.source

import androidx.annotation.Size
import com.training.companion.data.models.BodyPart
import com.training.companion.data.models.Exercise
import com.training.companion.data.models.intermediate.FullExerciseExtras
import com.training.companion.data.room.dao.ExerciseDao
import com.training.companion.data.room.dao.ExerciseExtrasDao

class ExercisesDataSource(
    private val exerciseDao: ExerciseDao,
    private val extrasDao: ExerciseExtrasDao
) {

    suspend fun getExercise(id: Int): Exercise {
        return exerciseDao.getExercise(id)
    }

    suspend fun getIconList(topExerciseId: Int?, workoutTypeId: Int, equipmentIds: IntArray?): List<Exercise> {
        return exerciseDao.getIconExercises(
            topExerciseId = topExerciseId,
            workoutTypeId = workoutTypeId,
            equipmentIds = equipmentIds
        )
    }

    suspend fun searchByNameWithIcon(
        query: String,
        workoutTypeId: Int,
        @Size(min = 1) equipmentIds: IntArray?
    ): List<Exercise> {
        return exerciseDao.searchByNameIconExercises(query, workoutTypeId, equipmentIds)
    }

    suspend fun getExtras(exerciseId: Int): FullExerciseExtras {
        return extrasDao.get(exerciseId)
    }

    suspend fun getTrainedBodyParts(exerciseIds: IntArray): List<BodyPart> {
        return extrasDao.getTrainedPrimaryBodyParts(exerciseIds)
    }
}