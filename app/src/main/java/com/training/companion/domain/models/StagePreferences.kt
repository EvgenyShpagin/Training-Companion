package com.training.companion.domain.models

data class StagePreferences(
    val timeCountingMethod: TimeCountingMethod,
    val initTime: Time,
    val resumeTime: Time = initTime,
) {
    companion object {
        val defaultStopwatch = StagePreferences(
            timeCountingMethod = TimeCountingMethod.Stopwatch,
            initTime = Time.ZERO,
            resumeTime = Time.ZERO
        )
    }
}