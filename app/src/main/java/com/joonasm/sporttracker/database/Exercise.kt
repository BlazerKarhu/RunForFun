package com.joonasm.sporttracker.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        onDelete = ForeignKey.CASCADE,
        parentColumns = ["uid"],
        childColumns = ["user"]
    )]
)

data class ExerciseInfo(
    val user: Long,
    val type: String,
    @PrimaryKey
    val date: String,
    val duration: Float,
    val distance: Float,
) {
    override fun toString() = "Exercise $type: $duration $distance"
}
