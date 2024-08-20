package com.training.companion.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.training.companion.data.models.BodyPart
import com.training.companion.data.models.intermediate.FullExerciseExtras

@Dao
abstract class ExerciseExtrasDao : TextContentDao {

    @Query(
        """
        SELECT 
            ee.exerciseId,
            ee.isRepeatable,
            ee.isAdjustableWeight,
            ee.exerciseVideoUrl,
            eq.equipmentId,
            e.textContentId AS equipmentTextContentId,
            et.workoutTypeId
        FROM
            exercise_extra ee
            JOIN exercise_type et ON ee.exerciseId = et.exerciseId
            JOIN workout_type wt ON et.workoutTypeId = wt.id
            JOIN exercise_equipment eq ON ee.exerciseId = eq.exerciseId
            JOIN equipment e ON eq.equipmentId = e.id
        WHERE
            ee.exerciseId = :exerciseId
        """
    )
    abstract suspend fun get(exerciseId: Int): FullExerciseExtras

    @Query(
        """
        SELECT NOT EXISTS(
	        SELECT 
                exercise_equipment.equipmentId 
            FROM 
                exercise_equipment 
            WHERE
                exercise_equipment.exerciseId IN (:exerciseIds)
                AND exercise_equipment.equipmentId = 1
        )
    """
    )
    abstract suspend fun areExercisesNoNeedEquipment(exerciseIds: IntArray): Boolean

    @Query(
        """
        SELECT DISTINCT
            pm.muscleId
        FROM 
            exercise_primary_muscle pm
        WHERE 
            pm.exerciseId IN (:exerciseIds)
    """
    )
    protected abstract suspend fun getExercisesPrimaryMuscleIds(exerciseIds: IntArray): IntArray

    @Query(
        """
        SELECT DISTINCT
            body_part.id,
            body_part.textContentId
        FROM 
            muscle
            JOIN body_part ON body_part.id = muscle.bodyPartId
        WHERE 
            muscle.muscleId IN (:muscleIds)
    """
    )
    protected abstract suspend fun getExercisesPrimaryBodyParts(muscleIds: IntArray): List<BodyPart>

    @Transaction
    open suspend fun getTrainedPrimaryBodyParts(exerciseIds: IntArray): List<BodyPart> {
        val primaryMuscleIds = getExercisesPrimaryMuscleIds(exerciseIds)
        return getExercisesPrimaryBodyParts(primaryMuscleIds)
    }
}