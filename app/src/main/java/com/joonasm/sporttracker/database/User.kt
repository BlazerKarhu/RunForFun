package com.joonasm.sporttracker.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val firstname: String,
    val lastname: String,
    val height: Int,
    val weight: Int
) {
    override fun toString() = "User: $firstname $lastname"
}

class UserExercise {
    @Embedded
    var user: User? = null

    @Relation(parentColumn = "uid", entityColumn = "user")
    var exercises: List<ExerciseInfo>? = null
}