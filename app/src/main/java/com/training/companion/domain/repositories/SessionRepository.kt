package com.training.companion.domain.repositories

import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.CompletedSet
import com.training.companion.domain.models.PassedStageInfo
import com.training.companion.domain.models.StagePreferences
import com.training.companion.domain.models.Time
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun setSessionPrefs(sessionPrefs: SessionPrefs)
    fun getSessionPrefs(): SessionPrefs?
    suspend fun setTotalTime(time: Time)
    fun getTotalTime(): Flow<Time>

    fun setStage(stage: WorkoutStage, onetimePreferences: StagePreferences? = null)
    fun getStage(): Flow<WorkoutStage>
    fun getPreviousStage(): WorkoutStage?
    fun getCurrentStage(): WorkoutStage

    fun getStagePreferences(stage: WorkoutStage): StagePreferences
    fun setStagePreferences(stage: WorkoutStage, prefs: StagePreferences)

    fun getStageTime(): Flow<Time>
    suspend fun setStageTime(time: Time)
    fun getPreviousStageDuration(): Time?

    fun setWorkoutSet(set: CompletedSet?, exerciseOrdinal: Int = LAST_EXERCISE_ORDINAL)

    fun getStageInfoOfLast(stage: WorkoutStage): PassedStageInfo?
    fun getPassedStages(): List<PassedStageInfo>
    fun getCurrentSetOrdinal(): Int
    fun getLastCompletedSet(): Flow<CompletedSet?>
    fun removePassedStage(index: Int)
    suspend fun finishSession()

    companion object {
        const val LAST_EXERCISE_ORDINAL = -1
        const val LAST_PASSED_STAGE = -2
    }
}