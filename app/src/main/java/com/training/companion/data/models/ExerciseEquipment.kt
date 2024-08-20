package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import com.training.companion.data.models.ExerciseEquipment.Companion.TABLE_NAME

/**
 * Cross-reference Table for Exercise and Equipment
 */
@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["exerciseId", "equipmentId"],
    foreignKeys = [
        ForeignKey(
            Exercise::class,
            ["exerciseId"],
            ["exerciseId"]
        ),
        ForeignKey(
            Equipment::class,
            ["id"],
            ["equipmentId"]
        )
    ]
)
data class ExerciseEquipment(
    val exerciseId: Int,
    val equipmentId: Int,
) {
    companion object {
        const val TABLE_NAME = "exercise_equipment"
    }
}
