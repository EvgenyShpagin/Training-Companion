package com.training.companion.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.training.companion.data.repositories.ActionsRepositoryImpl
import com.training.companion.data.repositories.AppSettings
import com.training.companion.data.repositories.EquipmentRepositoryImpl
import com.training.companion.data.repositories.ExercisesRepositoryImpl
import com.training.companion.data.repositories.MusclesRepositoryImpl
import com.training.companion.data.repositories.PlanRepositoryImpl
import com.training.companion.data.repositories.SessionRepositoryImpl
import com.training.companion.data.repositories.WorkoutRepositoryImpl
import com.training.companion.data.room.db.AppDatabase
import com.training.companion.data.source.ActionDataSource
import com.training.companion.data.source.EquipmentDataSource
import com.training.companion.data.source.ExercisesDataSource
import com.training.companion.data.source.MusclesDataSource
import com.training.companion.data.source.PlanDataSource
import com.training.companion.data.source.SessionDataSource
import com.training.companion.data.source.TextContentDataSource
import com.training.companion.data.source.WorkoutDataSource

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppSettings.initSharedPrefs(this)

        val sessionDataSource = SessionDataSource()
        SessionRepositoryImpl.initialize(sessionDataSource)

        val actionsDataSource = ActionDataSource()
        ActionsRepositoryImpl.initialize(actionsDataSource)

        AppDatabase.initialize(applicationContext)

        val textContentDataSource = TextContentDataSource(AppDatabase.get().getTextContentDao())

        val planDataSource = PlanDataSource(AppDatabase.get().getPlanDao())
        PlanRepositoryImpl.initialize(planDataSource, textContentDataSource)

        val musclesDataSource = MusclesDataSource(AppDatabase.get().getMuscleDao())
        MusclesRepositoryImpl.initialize(musclesDataSource, textContentDataSource)

        val exercisesDataSource = ExercisesDataSource(
            AppDatabase.get().getExerciseDao(),
            AppDatabase.get().getExerciseExtrasDao()
        )
        ExercisesRepositoryImpl.initialize(exercisesDataSource, textContentDataSource)

        val equipmentDataSource = EquipmentDataSource(AppDatabase.get().getEquipmentDao())
        EquipmentRepositoryImpl.initialize(equipmentDataSource, textContentDataSource)

        val workoutDataSource = WorkoutDataSource(AppDatabase.get().getWorkoutDao())
        WorkoutRepositoryImpl.initialize(workoutDataSource, textContentDataSource)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                WorkoutService.NOTIFICATION_CHANNEL_ID,
                WorkoutService.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.setSound(null, null)
            notificationChannel.setShowBadge(true)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}