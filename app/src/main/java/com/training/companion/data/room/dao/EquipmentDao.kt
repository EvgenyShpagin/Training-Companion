package com.training.companion.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.training.companion.data.models.Equipment

@Dao
interface EquipmentDao {

    @Query(
        """
        SELECT
            e.id,
            e.textContentId
        FROM 
            equipment e
            JOIN workout_type_equipment wte ON wte.equipmentId = e.id
        WHERE
            wte.workoutTypeId = :workoutTypeId
    """
    )
    suspend fun getByWorkoutType(workoutTypeId: Int): List<Equipment>
}