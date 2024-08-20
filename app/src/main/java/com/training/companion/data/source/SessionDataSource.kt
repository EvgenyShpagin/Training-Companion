package com.training.companion.data.source

import android.util.Log
import com.training.companion.data.models.SessionPrefs
import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.PassedStageInfo
import com.training.companion.domain.models.StagePreferences
import com.training.companion.domain.models.Time
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private val TAG = SessionDataSource::class.java.simpleName

class SessionDataSource {

    private val _stageTime = MutableStateFlow(value = Time.ZERO)
    private val stageTime = _stageTime.asStateFlow()

    private val _totalTime = MutableStateFlow(value = Time.ZERO)
    private val totalTime = _totalTime.asStateFlow()

    private var currentStage = WorkoutStage.Finished
    private val _stage = MutableStateFlow(currentStage)
    private val stage = _stage.asStateFlow()

    private val passedStages = mutableListOf<PassedStageInfo>()

    private val allStagePreferences = mutableMapOf<WorkoutStage, StagePreferences?>(
        WorkoutStage.WarmUp to null,
        WorkoutStage.Finished to null,
        WorkoutStage.Suspense to null,
        WorkoutStage.Exercise to null,
        WorkoutStage.Rest to null,
        WorkoutStage.Stretching to null,
    )

    private var sessionPrefs: SessionPrefs? = null

    fun setSessionPrefs(sessionPrefs: SessionPrefs?) {
        this.sessionPrefs = sessionPrefs
    }

    fun getSessionPrefs(): SessionPrefs? {
        return sessionPrefs
    }

    suspend fun setTotalTime(time: Time) {
        _totalTime.emit(time)
    }

    fun getTotalTime(): Flow<Time> {
        return totalTime
    }

    fun setStage(stage: WorkoutStage, passedStageInfo: PassedStageInfo?) {
        val DEBUG_currentStage = currentStage
        val DEBUG_newStage = stage
        currentStage = stage
        _stage.update { stage }
        if (DEBUG_newStage == DEBUG_currentStage) {
            _stage.value = _stage.value
        }
        passedStageInfo?.let { passedStages.add(it) }
    }

    fun getStage(): Flow<WorkoutStage> {
        return stage
    }

    fun getCurrentStage(): WorkoutStage {
        return currentStage
    }

    fun getStagePreferences(stage: WorkoutStage): StagePreferences? {
        Log.d(TAG, "getStagePreferences: $stage")
        return allStagePreferences[stage]
    }

    fun setStagePreferences(stage: WorkoutStage, preferences: StagePreferences) {
        allStagePreferences[stage] = preferences
    }

    fun getStageTime(): Flow<Time> {
        return stageTime
    }

    fun getStageTimeValue(): Time {
        return stageTime.value
    }

    suspend fun setStageTime(time: Time) {
        _stageTime.emit(time)
    }

    fun getPassedStages(): MutableList<PassedStageInfo> {
        return passedStages
    }

    private companion object {
//        const val KEY_SAVED = "saved"
//        const val KEY_PLAN_ID = "plan-id"
//        const val KEY_SESSION_WITH_PLAN = "session-with-plan"
//        const val KEY_TYPE = "type"
//        const val KEY_PLACE = "place"
//        const val KEY_REST_TIME = "rest"
//        const val VALUE_TIME_IS_NULL = -1
    }
}