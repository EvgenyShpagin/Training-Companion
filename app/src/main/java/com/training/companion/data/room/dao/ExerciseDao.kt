package com.training.companion.data.room.dao

import androidx.annotation.Size
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.training.companion.data.models.Exercise

@Dao
abstract class ExerciseDao {

    @Query("""
        SELECT 
            e.exerciseId,
            e.nameTextContentId,
            e.imageTextContentId
        FROM 
            exercise e
        WHERE
            e.exerciseId = :id
    """)
    abstract suspend fun getExercise(id: Int): Exercise

    @Transaction
    open suspend fun searchByNameIconExercises(
        searchWord: String,
        workoutTypeId: Int? = null,
        equipmentIds: IntArray? = null,
    ): List<Exercise> {
        val exerciseIds = _searchByNameExerciseIDs(searchWord, workoutTypeId, equipmentIds)
        return _getExercisesByIds(exerciseIds, null)
    }

    @Transaction
    open suspend fun getIconExercises(
        topExerciseId: Int?,
        workoutTypeId: Int? = null,
        equipmentIds: IntArray? = null,
    ): List<Exercise> {
        val requiredExerciseIDs = _getExerciseIDs(workoutTypeId, equipmentIds)
        return _getExercisesByIds(
            exerciseIds = requiredExerciseIDs,
            topExerciseId = topExerciseId
        )
    }

    @Query(
        """
        SELECT 
            e.exerciseId
        FROM 
            exercise e
            JOIN text_content tc ON e.nameTextContentId = tc.id
        WHERE 
            originalText LIKE :searchWord
    """
    )
    protected abstract suspend fun _getExerciseIDsByNameSimilarity(searchWord: String): IntArray

    @Query(
        """
        SELECT 
            e.exerciseId,
            e.nameTextContentId,
            e.imageTextContentId
        FROM 
            exercise e
        WHERE
            e.exerciseId IN (:exerciseIds)
        ORDER BY
            CASE
                WHEN e.exerciseId = :topExerciseId THEN 1 ELSE 2
            END
    """
    )
    protected abstract suspend fun _getExercisesByIds(
        exerciseIds: IntArray,
        topExerciseId: Int?,
    ): List<Exercise>

    @Query(
        """
        SELECT 
            et.exerciseId 
        FROM 
            exercise_type et 
        WHERE 
            et.exerciseId IN (:exerciseIds) 
            AND et.workoutTypeId = :workoutTypeId
    """
    )
    protected abstract suspend fun _getExerciseIDsSubsetByWorkoutType(
        exerciseIds: IntArray,
        workoutTypeId: Int,
    ): IntArray

    @Query(
        """
        SELECT 
            ee.exerciseId 
        FROM 
            exercise_equipment ee 
        WHERE 
            ee.exerciseId IN (:exerciseIds) 
            AND ee.equipmentId IN (:equipmentIds)
    """
    )
    protected abstract suspend fun _getExerciseIDsSubsetByEquipment(
        exerciseIds: IntArray,
        equipmentIds: IntArray,
    ): IntArray

    @Transaction
    protected open suspend fun _searchByNameExerciseIDs(
        searchWord: String,
        workoutTypeId: Int? = null,
        equipmentIds: IntArray? = null,
    ): IntArray {
        val exerciseIds = _getExerciseIDsByNameSimilarity(searchWord)
        val exerciseSubsetByWorkoutType = workoutTypeId?.let {
            _getExerciseIDsSubsetByWorkoutType(
                exerciseIds = exerciseIds,
                workoutTypeId = it
            )
        }
        val exerciseSubsetByEquipment = equipmentIds?.let {
            val currentExerciseIds = exerciseSubsetByWorkoutType ?: exerciseIds
            _getExerciseIDsSubsetByEquipment(
                exerciseIds = currentExerciseIds,
                equipmentIds = it
            )
        }
        return exerciseSubsetByEquipment ?: exerciseSubsetByWorkoutType ?: exerciseIds
    }

    @Transaction
    protected open suspend fun _getExerciseIDs(
        workoutTypeId: Int? = null,
        @Size(min = 1L) equipmentIds: IntArray? = null,
    ): IntArray {
        val allExerciseIds = _getAllExerciseIDs()
        val exerciseSubsetByWorkoutType = workoutTypeId?.let {
            _getExerciseIDsSubsetByWorkoutType(
                exerciseIds = allExerciseIds,
                workoutTypeId = it
            )
        }
        val exerciseSubsetByEquipment = equipmentIds?.let {
            val currentExerciseIds = exerciseSubsetByWorkoutType ?: allExerciseIds
            _getExerciseIDsSubsetByEquipment(
                exerciseIds = currentExerciseIds,
                equipmentIds = it
            )
        }
        return exerciseSubsetByEquipment ?: exerciseSubsetByWorkoutType ?: allExerciseIds
    }

    @Query("SELECT e.exerciseId FROM exercise e")
    protected abstract suspend fun _getAllExerciseIDs(): IntArray

}