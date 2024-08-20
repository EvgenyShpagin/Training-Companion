package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.training.companion.data.models.Workout.Companion.TABLE_NAME
import com.training.companion.domain.enums.WorkoutPlace
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.Datetime

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            Plan::class,
            ["id"],
            ["usedPlanId"]
        )
    ]
)
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val datetime: Datetime,
    val place: WorkoutPlace,
    val totalTimeSec: Int,
    val type: WorkoutType,
    val usedPlanId: Int?,
) {
    companion object {
        const val TABLE_NAME = "workout"
    }
}