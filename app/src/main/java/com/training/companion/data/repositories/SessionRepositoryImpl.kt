package com.training.companion.data.repositories

import com.training.companion.data.models.SessionPrefs
import com.training.companion.data.source.SessionDataSource
import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.CompletedSet
import com.training.companion.domain.models.PassedStageInfo
import com.training.companion.domain.models.StagePreferences
import com.training.companion.domain.models.Time
import com.training.companion.domain.models.TimeCountingMethod
import com.training.companion.domain.repositories.SessionRepository
import com.training.companion.domain.repositories.SessionRepository.Companion.LAST_EXERCISE_ORDINAL
import com.training.companion.domain.repositories.SessionRepository.Companion.LAST_PASSED_STAGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


class SessionRepositoryImpl private constructor(
    private val dataSource: SessionDataSource,
) : SessionRepository {

    private var onetimePreferencesSwap: OnetimeStagePreferenceSwap? = null

    private val _lastCompletedSet = MutableStateFlow<CompletedSet?>(null)

    override fun setSessionPrefs(sessionPrefs: SessionPrefs) {
        if (dataSource.getSessionPrefs() == null) {
            setDefaultStagePreferences()
        }
        dataSource.setSessionPrefs(sessionPrefs)
        when (sessionPrefs) {
            is SessionPrefs.WithPlan -> {
                dataSource.setStagePreferences(
                    stage = WorkoutStage.Rest,
                    preferences = sessionPrefs.plan.planExercises[0].set.restTime?.let {
                        StagePreferences(TimeCountingMethod.Countdown, it)
                    } ?: StagePreferences.defaultStopwatch
                )
            }

            is SessionPrefs.WithoutPlan -> {
                dataSource.setStagePreferences(
                    stage = WorkoutStage.Rest,
                    preferences = if (sessionPrefs.restTime != null) {
                        StagePreferences(TimeCountingMethod.Countdown, sessionPrefs.restTime)
                    } else {
                        StagePreferences.defaultStopwatch
                    }
                )
            }
        }
    }

    override fun getSessionPrefs(): SessionPrefs? {
        return dataSource.getSessionPrefs()
    }

    override suspend fun setTotalTime(time: Time) {
        dataSource.setTotalTime(time)
    }

    override fun getTotalTime(): Flow<Time> {
        return dataSource.getTotalTime()
    }

    override fun setStage(stage: WorkoutStage, onetimePreferences: StagePreferences?) {
        swapOnetimePreferencesToOriginal(currentStage = getCurrentStage())
        if (onetimePreferences != null) {
            setOnetimeStagePreferences(stage, onetimePreferences)
        } else if (dataSource.getSessionPrefs() is SessionPrefs.WithPlan) {
            setStagePreferencesFollowingPlan(stage)
        }

        val currentStage = dataSource.getCurrentStage()

        val passedStageInfo = when {
            currentStage != WorkoutStage.Finished && currentStage != stage -> {
                createPassedStageInfo()
                // проверить ситуацию : переход с warm-up на паузу и наоборот (несколько раз) passedStageInfo дублируется
            }

            else -> null
        }
        dataSource.setStage(stage, passedStageInfo)
    }

    private fun createPassedStageInfo(): PassedStageInfo {
        val finishingStage = dataSource.getCurrentStage()
        return PassedStageInfo(
            stage = finishingStage,
            duration = Time.duration(
                dataSource.getStageTimeValue(),
                dataSource.getStagePreferences(finishingStage)!!.initTime
            ),
            workoutSet = null
        )
    }

    private fun setStagePreferencesFollowingPlan(stage: WorkoutStage) {
        val sessionPrefs = dataSource.getSessionPrefs() as SessionPrefs.WithPlan

        fun setCountdownPreferencesOrDefaultStopwatch(stage: WorkoutStage, initTime: Time?) {
            dataSource.setStagePreferences(
                stage = stage,
                preferences = if (initTime != null) {
                    StagePreferences(TimeCountingMethod.Countdown, initTime)
                } else {
                    StagePreferences.defaultStopwatch
                }
            )
        }

        when (stage) {
            WorkoutStage.Exercise -> {
                val nextSetOrdinal = getCurrentSetOrdinal() + 1
                val nextSet = sessionPrefs.plan.getSetByOrdinal(nextSetOrdinal) ?: return
                setCountdownPreferencesOrDefaultStopwatch(stage, nextSet.duration)
            }

            WorkoutStage.Rest -> {
                val currentSetOrdinal = getCurrentSetOrdinal()
                val currentSet = sessionPrefs.plan.getSetByOrdinal(currentSetOrdinal) ?: return
                setCountdownPreferencesOrDefaultStopwatch(stage, currentSet.restTime)
            }

            else -> return
        }
    }

    override fun removePassedStage(index: Int) {
        val passedStage = dataSource.getPassedStages()
        if (index == LAST_PASSED_STAGE) {
            passedStage.removeLast()
        } else {
            passedStage.removeAt(index)
        }
    }

    override fun getStage(): Flow<WorkoutStage> {
        return dataSource.getStage()
    }

    override fun getPreviousStage(): WorkoutStage? {
        return dataSource.getPassedStages().lastOrNull()?.stage
    }

    override fun getCurrentStage(): WorkoutStage {
        return dataSource.getCurrentStage()
    }

    override fun getStagePreferences(stage: WorkoutStage): StagePreferences {
        return dataSource.getStagePreferences(stage)!!
    }

    override fun setStagePreferences(stage: WorkoutStage, prefs: StagePreferences) {
        dataSource.setStagePreferences(stage, prefs)
    }

    override suspend fun finishSession() {
        dataSource.setSessionPrefs(null)
        dataSource.setStage(WorkoutStage.Finished, null)
        dataSource.setTotalTime(Time.ZERO)
        dataSource.setStageTime(Time.ZERO)
        dataSource.getPassedStages().clear()
        onetimePreferencesSwap = null
        _lastCompletedSet.update { null }
    }

    private fun setOnetimeStagePreferences(stage: WorkoutStage, prefs: StagePreferences) {
        OnetimeStagePreferenceSwap(stage, prefs).also { swap ->
            onetimePreferencesSwap = swap
            swap.execute()
        }
    }

    override fun getStageTime(): Flow<Time> {
        return dataSource.getStageTime()
    }

    override suspend fun setStageTime(time: Time) {
        dataSource.setStageTime(time)
    }

    override fun getPreviousStageDuration(): Time? {
        return dataSource.getPassedStages().lastOrNull()?.duration
    }

    override fun setWorkoutSet(set: CompletedSet?, exerciseOrdinal: Int) {
        if (exerciseOrdinal == LAST_EXERCISE_ORDINAL) {
            setWorkoutSetToLastExerciseStage(set)
            if (set != null) {
                _lastCompletedSet.update { set }
            }
        } else {
            val passedStages = dataSource.getPassedStages()
            var currentOrdinal = 0
            passedStages.forEachIndexed { index, stageInfo ->
                if (stageInfo.stage == WorkoutStage.Exercise) {
                    if (exerciseOrdinal == currentOrdinal) {
                        passedStages[index] = passedStages[index].copy(workoutSet = set)
                        return
                    }
                    ++currentOrdinal
                }
            }
        }
    }

    override fun getCurrentSetOrdinal(): Int {
        val lastCompletedOrdinal = getPassedStages().count { it.stage == WorkoutStage.Exercise } - 1
        val currentStageIsExercise = getCurrentStage() == WorkoutStage.Exercise
        return when (lastCompletedOrdinal) {
            -1 -> 0
            else -> if (currentStageIsExercise) {
                lastCompletedOrdinal + 1
            } else {
                lastCompletedOrdinal
            }
        }
    }

    override fun getStageInfoOfLast(stage: WorkoutStage): PassedStageInfo? {
        return dataSource.getPassedStages().lastOrNull { it.stage == stage }
    }

    override fun getPassedStages(): List<PassedStageInfo> {
        return dataSource.getPassedStages()
    }

    override fun getLastCompletedSet(): Flow<CompletedSet?> {
        return _lastCompletedSet
    }

    private fun swapOnetimePreferencesToOriginal(currentStage: WorkoutStage) {
        onetimePreferencesSwap?.let { swap ->
            if (currentStage == swap.stage) {
                swap.execute() // back to original
                onetimePreferencesSwap = null
            }
        }
    }

    private fun setWorkoutSetToLastExerciseStage(set: CompletedSet?) {
        val passedStages = dataSource.getPassedStages()
        val lastExerciseIndex = passedStages.indexOfLast { it.stage == WorkoutStage.Exercise }
        if (lastExerciseIndex == -1) return
        passedStages[lastExerciseIndex] = passedStages[lastExerciseIndex].copy(workoutSet = set)
    }

    private fun setDefaultStagePreferences() {
        dataSource.setStagePreferences(WorkoutStage.WarmUp, StagePreferences.defaultStopwatch)
        dataSource.setStagePreferences(WorkoutStage.Suspense, StagePreferences.defaultStopwatch)
        dataSource.setStagePreferences(WorkoutStage.Exercise, StagePreferences.defaultStopwatch)
        dataSource.setStagePreferences(WorkoutStage.Stretching, StagePreferences.defaultStopwatch)
        dataSource.setStagePreferences(WorkoutStage.Rest, StagePreferences.defaultStopwatch)
    }

    private inner class OnetimeStagePreferenceSwap(
        val stage: WorkoutStage,
        private val oneTimePreferences: StagePreferences,
    ) {
        val originalPreferences = getStagePreferences(stage)
        private var onetimeSet = false
        fun execute() {
            if (!onetimeSet) {
                setStagePreferences(stage, oneTimePreferences)
                onetimeSet = true
            } else {
                setStagePreferences(stage, originalPreferences)
            }
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: SessionRepository? = null

        fun initialize(dataSource: SessionDataSource) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = SessionRepositoryImpl(dataSource)
                }
            }
        }

        fun get(): SessionRepository {
            return INSTANCE ?: throw IllegalStateException("SessionRepository must be initialized")
        }
    }
}