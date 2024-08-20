package com.training.companion.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.training.companion.data.models.TextContent.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class TextContent(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val originalText: String,
) {
    companion object {
        const val TABLE_NAME = "text_content"
    }
}
