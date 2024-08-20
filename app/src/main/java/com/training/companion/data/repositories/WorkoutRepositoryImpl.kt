package com.training.companion.data.repositories

import com.training.companion.data.source.TextContentDataSource
import com.training.companion.data.source.WorkoutDataSource
import com.training.companion.data.util.get
import com.training.companion.domain.enums.Language
import com.training.companion.domain.repositories.WorkoutRepository
import com.training.companion.toDomainWorkout
import com.training.companion.domain.models.PastWorkout as DomainPastWorkout


class WorkoutRepositoryImpl private constructor(
    private val dataSource: WorkoutDataSource,
    private val textDataSource: TextContentDataSource,
) : WorkoutRepository {

    override suspend fun getAllWorkouts(language: Language): List<DomainPastWorkout> {
        val pastWorkouts = dataSource.getPastWorkouts()
        return pastWorkouts.map { workout ->
            val exerciseNameIds = workout.sets.map { it.exerciseNameTextId }
            val exerciseNames = textDataSource.get(exerciseNameIds, language)
            val exerciseFilenameIds = workout.sets.map { it.exerciseIconFilenameTextId }
            val exerciseFilenames = textDataSource.get(exerciseFilenameIds, language)
            val bodyPartNames = textDataSource.get(workout.includedBodyPart.map { it.textContentId }, language)
            workout.toDomainWorkout(exerciseNames, exerciseFilenames, bodyPartNames)
        }
    }

    override suspend fun addWorkout(workout: DomainPastWorkout) {
        TODO("IN FUTURE")
    }

    companion object {

        @Volatile
        private var INSTANCE: WorkoutRepositoryImpl? = null

        fun initialize(dataSource: WorkoutDataSource, textDataSource: TextContentDataSource) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = WorkoutRepositoryImpl(dataSource, textDataSource)
                }
            }
        }

        fun get(): WorkoutRepository {
            return INSTANCE ?: throw IllegalStateException("WorkoutRepository must be initialized")
        }
    }
}