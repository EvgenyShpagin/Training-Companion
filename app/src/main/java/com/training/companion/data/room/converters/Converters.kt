package com.training.companion.data.room.converters

import androidx.room.TypeConverter
import com.training.companion.domain.enums.WorkoutType
import com.training.companion.domain.models.Datetime
import com.training.companion.domain.models.Reps


class Converters {

    @TypeConverter
    fun fromReps(reps: Reps?): String? {
        return reps?.toString()
    }

    @TypeConverter
    fun toReps(str: String?): Reps? {
        return str?.let { Reps.parseFromString(it) }
    }

    @TypeConverter
    fun toSimpleDate(str: String?): Datetime? {
        str ?: return null
        val values = str.split(' ', '/', ':').map { it.toInt() }
        return Datetime(
            dayOfMonth = values[0],
            month = values[1],
            year = values[2],
            hour = values[3],
            minute = values[4],
        )
    }

    @TypeConverter
    fun fromSimpleDate(datetime: Datetime?): String? {
        return datetime?.toString()
    }

    @TypeConverter
    fun toWorkoutType(ordinal: Int): WorkoutType {
        return WorkoutType.entries[ordinal]
    }

    @TypeConverter
    fun fromWorkoutType(type: WorkoutType): Int {
        return type.ordinal
    }
}