package com.training.companion.data.repositories

import com.training.companion.data.source.ExercisesDataSource
import com.training.companion.data.source.TextContentDataSource
import com.training.companion.data.util.get
import com.training.companion.domain.enums.Language
import com.training.companion.domain.models.BodyPart
import com.training.companion.domain.models.Exercise
import com.training.companion.domain.models.ExerciseExtras
import com.training.companion.domain.models.ExerciseListFilter
import com.training.companion.domain.models.IconExercise
import com.training.companion.domain.repositories.ExercisesRepository
import com.training.companion.idsArray
import com.training.companion.toDomainBodyPart
import com.training.companion.toDomainExercise
import com.training.companion.toDomainExtras
import com.training.companion.data.models.Exercise as DataExercise


class ExercisesRepositoryImpl private constructor(
    private val dataSource: ExercisesDataSource,
    private val textContentDataSource: TextContentDataSource,
) : ExercisesRepository {

    override suspend fun getExercise(id: Int, language: Language): IconExercise {
        val exercise = dataSource.getExercise(id)
        return exercise.toDomain(language)
    }

    override suspend fun searchForIconExercise(
        query: String,
        filter: ExerciseListFilter,
        language: Language,
    ): List<IconExercise> {
        val searchedExercises = dataSource.searchByNameWithIcon(
            query = query,
            workoutTypeId = filter.byWorkoutType.id,
            equipmentIds = filter.byEquipmentCode?.idsArray()
        )
        return searchedExercises.toDomain(language)
    }

    override suspend fun getExtras(
        exerciseId: Int, language: Language,
    ): ExerciseExtras {
        val extras = dataSource.getExtras(exerciseId)
        val equipmentName = textContentDataSource.get(extras.equipmentTextContentId, language)
        return extras.toDomainExtras(equipmentName)
    }

    override suspend fun getTrainedBodyParts(
        exercises: List<Exercise>,
        language: Language,
    ): List<BodyPart> {
        val exerciseIds = IntArray(exercises.size) { exercises[it].id }
        val trainedBodyParts = dataSource.getTrainedBodyParts(exerciseIds)
        val names = textContentDataSource.get(trainedBodyParts.map { it.textContentId }, language)
        return trainedBodyParts.mapIndexed { index, bodyPart -> bodyPart.toDomainBodyPart(names[index]) }
    }

    override suspend fun getIconList(
        topExerciseId: Int?,
        filter: ExerciseListFilter,
        language: Language,
    ): List<IconExercise> {
        return dataSource.getIconList(
            topExerciseId,
            filter.byWorkoutType.id,
            filter.byEquipmentCode?.idsArray()
        ).toDomain(language)
    }

    private suspend fun DataExercise.toDomain(language: Language): IconExercise {
        val iconFilename = textContentDataSource.get(imageTextContentId, language)
        val name = textContentDataSource.get(nameTextContentId, language)
        return toDomainExercise(name, iconFilename)
    }

    private suspend fun List<DataExercise>.toDomain(language: Language): List<IconExercise> {
        val iconFilename = textContentDataSource.get(map { it.imageTextContentId }, language)
        val name = textContentDataSource.get(map { it.nameTextContentId }, language)
        return mapIndexed { i, exercise -> exercise.toDomainExercise(name[i], iconFilename[i]) }
    }

    companion object {

        @Volatile
        private var INSTANCE: ExercisesRepositoryImpl? = null

        fun initialize(
            dataSource: ExercisesDataSource,
            textContentDataSource: TextContentDataSource,
        ) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = ExercisesRepositoryImpl(dataSource, textContentDataSource)
                }
            }
        }

        fun get(): ExercisesRepositoryImpl {
            return INSTANCE
                ?: throw IllegalStateException("ExercisesRepositoryImpl: must be initialized")
        }
    }
}