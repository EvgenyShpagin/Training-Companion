package com.training.companion.data.source

import com.training.companion.data.models.Muscle
import com.training.companion.data.room.dao.MuscleDao


class MusclesDataSource(private val dao: MuscleDao) {
    suspend fun getAll(): List<Muscle> {
        return dao.getAll()
    }
}