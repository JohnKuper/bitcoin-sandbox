package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.app.Application
import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import io.reactivex.Maybe
import org.jetbrains.anko.doAsync


class UsersRepository(app: Application) {

    private val db = SandboxDatabase.getInstance(app)
    private val userDao = db.userDao()

    val observableUsers = userDao.getAll()

    fun insert(user: User) {
        doAsync { userDao.insert(user) }
    }

    fun getByName(name: String): Maybe<User> = userDao.getByName(name)

    fun updateCurrentUser(old: User, new: User) {
        doAsync {
            db.runInTransaction {
                userDao.update(old.apply { isCurrent = false })
                userDao.update(new.apply { isCurrent = true })
            }
        }
    }
}
