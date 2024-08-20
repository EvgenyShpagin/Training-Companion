package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.training.companion.data.models.Plan.Companion.TABLE_NAME
import com.training.companion.domain.models.Datetime

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            WorkoutType::class,
            ["id"],
            ["workoutTypeId"]
        )
    ]
)
data class Plan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val comment: String?,
    val workoutTypeId: Int,
    val usedTimes: Int,
    val creationDate: Datetime?,
) {
    companion object {
        const val TABLE_NAME = "wplan"
    }
}