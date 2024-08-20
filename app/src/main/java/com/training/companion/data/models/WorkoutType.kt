package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.training.companion.data.models.WorkoutType.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            TextContent::class,
            ["id"],
            ["textContentId"]
        )
    ]
)
data class WorkoutType(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val textContentId: Int,
) {
    companion object {
        const val TABLE_NAME = "workout_type"
    }
}