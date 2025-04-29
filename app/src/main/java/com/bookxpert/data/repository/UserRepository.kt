package com.bookxpert.data.repository

import androidx.lifecycle.LiveData
import com.bookxpert.data.db.dao.UserDao
import com.bookxpert.data.db.entities.User

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    fun getUser(): LiveData<User> = userDao.getUser()
    suspend fun deleteUser(user: User) = userDao.delete(user)
}
