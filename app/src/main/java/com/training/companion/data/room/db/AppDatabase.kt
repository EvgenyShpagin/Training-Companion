package com.training.companion.data.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.training.companion.data.models.BodyPart
import com.training.companion.data.models.Equipment
import com.training.companion.data.models.Exercise
import com.training.companion.data.models.ExerciseEquipment
import com.training.companion.data.models.ExerciseExtras
import com.training.companion.data.models.ExercisePrimaryMuscles
import com.training.companion.data.models.ExerciseSecondaryMuscles
import com.training.companion.data.models.ExerciseType
import com.training.companion.data.models.Language
import com.training.companion.data.models.Muscle
import com.training.companion.data.models.Plan
import com.training.companion.data.models.Set
import com.training.companion.data.models.TextContent
import com.training.companion.data.models.Translation
import com.training.companion.data.models.Workout
import com.training.companion.data.models.WorkoutType
import com.training.companion.data.models.WorkoutTypeEquipment
import com.training.companion.data.room.converters.Converters
import com.training.companion.data.room.dao.EquipmentDao
import com.training.companion.data.room.dao.ExerciseDao
import com.training.companion.data.room.dao.ExerciseExtrasDao
import com.training.companion.data.room.dao.MuscleDao
import com.training.companion.data.room.dao.PlanDao
import com.training.companion.data.room.dao.TextContentDao
import com.training.companion.data.room.dao.WorkoutDao


private const val DATABASE_NAME = "workout"


@Database(
    entities = [
        ExercisePrimaryMuscles::class,
        ExerciseSecondaryMuscles::class,
        Set::class,
        Exercise::class,
        Muscle::class,
        Plan::class,
        Workout::class,
        Language::class,
        TextContent::class,
        Translation::class,
        WorkoutType::class,
        Equipment::class,
        ExerciseEquipment::class,
        WorkoutTypeEquipment::class,
        ExerciseType::class,
        ExerciseExtras::class,
        BodyPart::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getWorkoutDao(): WorkoutDao
    abstract fun getPlanDao(): PlanDao
    abstract fun getExerciseDao(): ExerciseDao
    abstract fun getMuscleDao(): MuscleDao
    abstract fun getExerciseExtrasDao(): ExerciseExtrasDao
    abstract fun getEquipmentDao(): EquipmentDao
    abstract fun getTextContentDao(): TextContentDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun initialize(appContext: Context) {
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    appContext.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).createFromAsset("database/workout.db").build()
            }
        }

        fun get(): AppDatabase {
            return INSTANCE ?: throw IllegalStateException("AppDatabase must be initialized")
        }
    }
}
