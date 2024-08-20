package com.training.companion.data.repositories

import com.training.companion.data.source.MusclesDataSource
import com.training.companion.data.source.TextContentDataSource
import com.training.companion.data.util.get
import com.training.companion.domain.enums.Language
import com.training.companion.domain.models.Muscle
import com.training.companion.domain.repositories.MusclesRepository
import com.training.companion.toDomainMuscle

class MusclesRepositoryImpl private constructor(
    private val dataSource: MusclesDataSource,
    private val textDataSource: TextContentDataSource,
) : MusclesRepository {

    override suspend fun getAll(language: Language): List<Muscle> {
        val muscles = dataSource.getAll()
        val muscleNameIds = muscles.map { it.textContentId }
        val muscleNames = textDataSource.get(muscleNameIds, language)
        return muscles.mapIndexed { index, muscle -> muscle.toDomainMuscle(muscleNames[index]) }
    }

    companion object {

        @Volatile
        private var INSTANCE: MusclesRepositoryImpl? = null

        fun initialize(dataSource: MusclesDataSource, textDataSource: TextContentDataSource) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = MusclesRepositoryImpl(dataSource, textDataSource)
                }
            }
        }

        fun get(): MusclesRepositoryImpl {
            return INSTANCE
                ?: throw IllegalStateException("MusclesRepositoryImpl: must be initialized")
        }
    }
}