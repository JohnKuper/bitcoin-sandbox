package com.kaizendeveloper.bitcoinsandbox.db

import android.app.Application
import io.reactivex.Maybe
import org.jetbrains.anko.doAsync


class SandboxRepository(app: Application) {

    private val db = SandboxDatabase.getInstance(app)
    private val userDao = db.userDao()

    val allUsers = userDao.getAllUsers()

    fun insert(user: User) {
        doAsync { userDao.insert(user) }
    }

    fun getByName(name: String): Maybe<User> = userDao.getByName(name)
}
