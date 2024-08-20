package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.training.companion.data.models.BodyPart.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME, foreignKeys = [
        ForeignKey(TextContent::class, ["id"], ["textContentId"])
    ]
)
data class BodyPart(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val textContentId: Int,
) {
    companion object {
        const val TABLE_NAME = "body_part"
    }
}
