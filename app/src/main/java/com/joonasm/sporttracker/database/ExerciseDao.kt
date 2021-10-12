package com.joonasm.sporttracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exerciseinfo WHERE user = :userid")
    // the @Relation do the INNER JOIN for you ;)
    fun getUserExercises(userid: Long): LiveData<List<ExerciseInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(exercise: ExerciseInfo): Long
}