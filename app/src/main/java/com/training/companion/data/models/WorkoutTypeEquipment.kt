package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import com.training.companion.data.models.WorkoutTypeEquipment.Companion.TABLE_NAME

/**
 * Cross-reference Table for Equipment and WorkoutType
 */
@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["equipmentId", "workoutTypeId"],
    foreignKeys = [
        ForeignKey(
            Equipment::class,
            ["id"],
            ["equipmentId"]
        ),
        ForeignKey(
            WorkoutType::class,
            ["id"],
            ["workoutTypeId"]
        )
    ]
)
data class WorkoutTypeEquipment(
    val equipmentId: Int,
    val workoutTypeId: Int,
) {
    companion object {
        const val TABLE_NAME = "workout_type_equipment"
    }
}
