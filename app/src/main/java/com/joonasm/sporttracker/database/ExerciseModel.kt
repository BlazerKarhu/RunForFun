package com.joonasm.sporttracker.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ExerciseModel(application: Application, uid: Long): AndroidViewModel(application) {
    private val userExercises: LiveData<List<ExerciseInfo>> =
        UserDB.get(getApplication()).exerciseDao().getUserExercises(uid)

    fun getExercises() = userExercises

    @DelicateCoroutinesApi
    fun insertContact(uid: Long, type: String, date: String, distance: Float, duration: Float) {
        GlobalScope.launch {
            val db = UserDB.get(getApplication())
            db.exerciseDao().insert(ExerciseInfo(uid, type, date, distance, duration))
        }
    }
}
class ContactModelFactory(private val application: Application, private val uid: Long) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ExerciseModel(application, uid) as T
}