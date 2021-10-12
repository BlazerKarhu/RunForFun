package com.joonasm.sporttracker.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserModel(application: Application) : AndroidViewModel(application) {
    private val users: LiveData<List<User>> = UserDB.get(getApplication()).userDao().getAll()

    fun getUsers() = users

    @DelicateCoroutinesApi
    fun insertUser(firstname: String, lastname: String, height: Int, weight: Int) {
        GlobalScope.launch {
            val db = UserDB.get(getApplication())
            db.userDao().insert(User(0, firstname, lastname, height, weight))
        }
    }
}