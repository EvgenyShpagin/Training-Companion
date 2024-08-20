package com.training.companion.domain.util

import com.training.companion.domain.enums.Language
import com.training.companion.domain.models.Datetime
import java.util.Calendar
import java.util.Locale


fun getSystemLanguage(): Language? {
    val systemLanguage = Locale.getDefault().isO3Language
    Language.entries.forEach { lang ->
        if (lang.iso3 == systemLanguage)
            return lang
    }
    return null
}

val Language.isOriginal get() = this == originalLanguage
val originalLanguage = Language.English

private const val EXERCISES_DIRECTORY = "exercises/"

fun getExerciseImageDirectoryPath(): String {
    return EXERCISES_DIRECTORY
}

fun Calendar.toDatetime(): Datetime {
    return Datetime(
        dayOfMonth = get(Calendar.DAY_OF_MONTH),
        month = get(Calendar.MONTH) + 1,
        year = get(Calendar.YEAR),
        hour = get(Calendar.HOUR_OF_DAY),
        minute = get(Calendar.MINUTE)
    )
}

fun getCurrentDate(): Datetime {
    val now = Calendar.getInstance()
    return now.toDatetime()
}

fun getYesterdayDate(): Datetime {
    val date = Calendar.getInstance()
    date.add(Calendar.DATE, -1)
    return date.toDatetime()
}