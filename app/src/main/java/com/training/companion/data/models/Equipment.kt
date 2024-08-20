package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.training.companion.data.models.Equipment.Companion.TABLE_NAME

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
data class Equipment(
    @PrimaryKey val id: Int,
    val textContentId: Int,
) {
    companion object {
        const val TABLE_NAME = "equipment"
    }
}
