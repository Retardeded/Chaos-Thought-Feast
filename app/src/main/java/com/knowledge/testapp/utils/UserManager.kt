package com.knowledge.testapp.utils

import com.knowledge.testapp.data.User

object UserManager {
    @Volatile private var user: User = User()

    fun getUser(): User {
        return user
    }

    @Synchronized
    fun setUser(newUser: User) {
        user = newUser
    }
}