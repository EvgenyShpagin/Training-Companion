package com.training.companion.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.training.companion.data.models.Muscle

@Dao
interface MuscleDao {

    @Query("SELECT * FROM muscle")
    suspend fun getAll(): List<Muscle>
}