package com.training.companion.presentation

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkBuilder
import com.training.companion.R
import com.training.companion.data.repositories.SessionRepositoryImpl
import com.training.companion.domain.enums.WorkoutStage
import com.training.companion.domain.models.Time
import com.training.companion.domain.models.TimeCountingMethod
import com.training.companion.domain.usecases.GetCurrentStageUseCase
import com.training.companion.domain.usecases.GetStagePreferencesUseCase
import com.training.companion.domain.usecases.GetStageUseCase
import com.training.companion.domain.usecases.SetStageOnFinishUseCase
import com.training.companion.domain.usecases.SetStageTimeUseCase
import com.training.companion.domain.usecases.SetStageUseCase
import com.training.companion.domain.usecases.SetTotalTimeUseCase
import com.training.companion.presentation.activities.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class WorkoutService : LifecycleService() {

    private val sessionRepository = SessionRepositoryImpl.get()
    private val setTotalTimeUseCase = SetTotalTimeUseCase(sessionRepository)
    private val setStageTimeUseCase = SetStageTimeUseCase(sessionRepository)
    private val getStageUseCase = GetStageUseCase(sessionRepository)
    private val getCurrentStageUseCase = GetCurrentStageUseCase(sessionRepository)
    private val getStagePreferencesUseCase = GetStagePreferencesUseCase(sessionRepository)
    private val setStageUseCase = SetStageUseCase(sessionRepository)
    private val setStageOnFinishUseCase = SetStageOnFinishUseCase(
        sessionRepository, setStageUseCase
    )

    private var currentStageTimeCountingMethod = TimeCountingMethod.Stopwatch
    private var currentStage = getCurrentStageUseCase()

    private var stageJustChanged = true

    companion object {
        const val START = "action_start"
        const val STOP = "action_stop"
        private const val NOTIFICATION_ID = 1

        const val NOTIFICATION_CHANNEL_ID = "Workout-time-notification"
        const val NOTIFICATION_CHANNEL_NAME = "Workout timer"
    }

    private val notificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setColorized(true)
            .setColor(Color.parseColor("#BEAEE2"))
            .setSmallIcon(R.drawable.ic_done_bold_outline_24)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
    }

    private fun getPendingIntentForStage(): PendingIntent {
        return NavDeepLinkBuilder(this)
            .setGraph(R.navigation.main_nav_graph)
            .setComponentName(MainActivity::class.java)
            .setDestination(
                destId = when (currentStage) {
                    WorkoutStage.WarmUp -> R.id.workoutFragment
                    WorkoutStage.Exercise -> R.id.workoutFragment
                    WorkoutStage.Rest -> R.id.workoutFragment
                    WorkoutStage.Stretching -> R.id.workoutFragment
                    WorkoutStage.Suspense -> R.id.suspenseFragment
                    WorkoutStage.Finished -> R.id.startWorkoutFragment
                }
            ).createPendingIntent()
    }


    private val tickTimer = Timer()

    private var totalTime = Time.ZERO
    private var stageTime = Time.ZERO

    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START -> start()
            STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        tickTimer.cancel()
        super.onDestroy()
    }

    private fun start() {
        checkStartState()
        lifecycleScope.launch {
            observeStageChange()
            initNotificationManager()
            scheduleTicking()
            startForeground(NOTIFICATION_ID, getNotificationForStage(currentStage))
        }
    }

    private fun observeStageChange() {
        lifecycleScope.launch(Dispatchers.Main) {
            getStageUseCase().collectLatest { stage ->
                Log.d("TAG_1", "observeStageChange: $stage")
                currentStage = stage
                stageJustChanged = true
                val stagePreferences = getStagePreferencesUseCase(stage)
                stageTime = stagePreferences.resumeTime
                currentStageTimeCountingMethod = stagePreferences.timeCountingMethod
                registerStageTransition(stage)
            }
        }
    }

    private suspend fun registerStageTransition(currentStage: WorkoutStage) {
        if (currentStageTimeCountingMethod != TimeCountingMethod.Countdown) return
        when (currentStage) {
            WorkoutStage.Rest -> setStageOnFinishUseCase(WorkoutStage.Exercise)
            WorkoutStage.Exercise -> setStageOnFinishUseCase(WorkoutStage.Rest)
            WorkoutStage.WarmUp -> setStageOnFinishUseCase(WorkoutStage.Exercise)
            else -> return
        }
    }

    private fun stop() {
        lifecycleScope.launch(Dispatchers.Main) {
            setStageUseCase(WorkoutStage.Finished)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun getNotificationForStage(currentStage: WorkoutStage): Notification {
        val contentText = getNotificationContentText()
        notificationBuilder.setContentText(contentText)

        if (stageJustChanged) {
            val title = getNotificationTitle(currentStage)
            val pendingIntent = getPendingIntentForStage()
            notificationBuilder.setContentTitle(title)
            notificationBuilder.setContentIntent(pendingIntent)
            stageJustChanged = false
        }

        return notificationBuilder.build()
    }

    private fun getNotificationTitle(stage: WorkoutStage): String {
        val title = when (stage) {
            WorkoutStage.WarmUp -> getString(R.string.workout_stage_warm_up)
            WorkoutStage.Exercise -> getString(R.string.workout_stage_exercise)
            WorkoutStage.Rest -> getString(R.string.workout_stage_rest)
            WorkoutStage.Stretching -> getString(R.string.workout_stage_stretching)
            WorkoutStage.Suspense -> getString(R.string.workout_stage_suspense)
            WorkoutStage.Finished -> getString(R.string.workout_stage_finish)
        }
        return title
    }

    private fun getNotificationContentText(): String {
        val hoursOfWorkout = totalTime.hours
        val minutesOfWorkout = totalTime.minutes

        return if (hoursOfWorkout != 0) {
            getString(R.string.time_HH_MM, hoursOfWorkout, minutesOfWorkout)
        } else {
            resources.getQuantityString(
                R.plurals.minutes_plural_label,
                minutesOfWorkout,
                minutesOfWorkout
            )
        }
    }

    private fun initNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private fun scheduleTicking() {
        tickTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                tick()
            }
        }, 0, 1000)
    }

    private fun tick() {
        lifecycleScope.launch {
            handleTotalTime()
            handleStageTime()
            updateNotification()
        }
    }

    private suspend fun handleTotalTime() {
        if (currentStage == WorkoutStage.Suspense) return
        setTotalTimeUseCase(time = totalTime)
        totalTime += 1
    }

    private suspend fun handleStageTime() {
        setStageTimeUseCase(time = stageTime)
        when (currentStageTimeCountingMethod) {
            TimeCountingMethod.Stopwatch -> stageTime += 1
            TimeCountingMethod.Countdown -> stageTime -= 1
        }
    }

    private fun updateNotification() {
        val notification = getNotificationForStage(currentStage)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun checkStartState() {
        assert(sessionRepository.getSessionPrefs() != null) { "Before workout start session prefs must be set" }
        assert(sessionRepository.getCurrentStage() != WorkoutStage.Finished) { "Before workout start stage must not be Finished" }
    }
}