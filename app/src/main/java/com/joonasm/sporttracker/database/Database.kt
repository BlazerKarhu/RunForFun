package com.joonasm.sporttracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(User::class), (ExerciseInfo::class)], version = 1)
abstract class UserDB : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        private var sInstance: UserDB? = null

        @Synchronized
        fun get(context: Context): UserDB {
            if (sInstance == null) {
                sInstance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        UserDB::class.java, "user.db"
                    ).build()
            }
            return sInstance!!
        }
    }
}