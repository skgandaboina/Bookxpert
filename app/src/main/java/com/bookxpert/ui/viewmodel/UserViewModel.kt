package com.bookxpert.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bookxpert.data.db.AppDatabase
import com.bookxpert.data.db.entities.User
import com.bookxpert.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }

    fun getUser(): LiveData<User> = repository.getUser()

    fun deleteUser(user: User) = viewModelScope.launch {
        repository.deleteUser(user)
    }
}
