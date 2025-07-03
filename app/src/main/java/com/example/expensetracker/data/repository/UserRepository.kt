package com.example.expensetracker.data.repository

import com.example.expensetracker.data.User
import com.example.expensetracker.data.UserDao

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUser(username: String, password: String): User? {
        return userDao.getUser(username, password)
    }
    suspend fun isUsernameTaken(username: String): Boolean {
        return userDao.getUserByUsername(username) != null
    }
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

}
