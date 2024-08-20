package com.training.companion.domain.models

import java.text.DateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


data class Datetime(
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
    val hour: Int,
    val minute: Int,
) {

    fun dateIsTheSame(other: Datetime): Boolean {
        return month == other.month && dayOfMonth == other.dayOfMonth && year == other.year
    }

    override fun toString(): String {
        return String.format(
            Locale.getDefault(),
            "%d/%d/%d %02d:%02d",
            dayOfMonth, month, year, hour, minute
        )
    }

    fun getFormattedDate(locale: Locale): String {
        val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale)

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, hour)

        val timeZone = TimeZone.getDefault()

        dateFormat.timeZone = timeZone
        return dateFormat.format(calendar.time)
    }
}