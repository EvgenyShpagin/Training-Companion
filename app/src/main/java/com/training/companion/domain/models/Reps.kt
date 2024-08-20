package com.training.companion.domain.models

sealed class Reps {
    data object Max : Reps()
    class Exact(val value: Int) : Reps() {
        override fun toString() = value.toString()
    }
    class Range(val value: IntRange) : Reps() {
        override fun toString() = value.toString()
    }

    companion object {
        fun parseFromString(str: String): Reps? {
            return if (str == Max.toString()) {
                Max
            } else if (str.contains("..")) {
                val firstNumber = str.substringBefore('.').toInt()
                val lastNumber = str.substringAfterLast('.').toInt()
                Range(firstNumber..lastNumber)
            } else {
                str.toIntOrNull()?.let { Exact(it) }
            }
        }
    }
}

