package com.training.companion.domain.models

import kotlin.math.abs


data class Time(val seconds: Int, val minutes: Int, val hours: Int) {
    constructor(totalSeconds: Int) : this(
        seconds = totalSeconds % 60,
        minutes = getMinutesFromSeconds(totalSeconds),
        hours = getHoursFromSeconds(totalSeconds)
    )

    val totalSeconds: Int
        get() = seconds + minutes * 60 + hours * 3600

    operator fun plus(seconds: Int): Time {
        return Time(totalSeconds = this.totalSeconds + seconds)
    }

    operator fun minus(seconds: Int): Time {
        return Time(totalSeconds = this.totalSeconds - seconds)
    }

    operator fun minus(other: Time): Time {
        val diffSeconds = this.totalSeconds - other.totalSeconds
        assert(diffSeconds >= 0)
        return Time(diffSeconds)
    }

    override fun toString(): String {
        return toString(format = Format.HH_MM_SS)
    }

    fun toString(format: Format): String {
        return when (format) {
            Format.HH_MM_SS -> String.format(format.pattern, hours, minutes, seconds)
            Format.H_MM_SS -> String.format(format.pattern, hours, minutes, seconds)
            Format.MM_SS -> String.format(format.pattern, minutes, seconds)
        }
    }

    enum class Format(val pattern: String) {
        HH_MM_SS(pattern = "%02d:%02d:%02d"),
        H_MM_SS(pattern = "%d:%02d:%02d"),
        MM_SS(pattern = "%02d:%02d")
    }

    companion object {
        fun fromString(timeString: String): Time {
            val match = timeRegex.find(timeString)
                ?: throw IllegalArgumentException("timeString is not representation of Time as string")
            val hours = match.groups["hrs"]?.value?.toInt() ?: 0
            val minutes = match.groups["min"]!!.value.toInt()
            val seconds = match.groups["sec"]!!.value.toInt()
            return Time(seconds = seconds, minutes = minutes, hours = hours)
        }

        fun getMinutesFromSeconds(seconds: Int): Int {
            return (seconds % 3600) / 60
        }

        fun getHoursFromSeconds(seconds: Int): Int {
            return seconds / 3600
        }

        fun duration(time1: Time, time2: Time): Time {
            return Time(abs(time1.totalSeconds - time2.totalSeconds))
        }

        val ZERO = Time(0)
        private val timeRegex = "\\b(?:(?<hrs>[0-5]?\\d):)?(?<min>[0-5]?\\d):(?<sec>[0-5]?\\d)\\b".toRegex()
    }
}