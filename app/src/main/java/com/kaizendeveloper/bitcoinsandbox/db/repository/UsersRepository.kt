package com.kaizendeveloper.bitcoinsandbox.db.repository

import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import com.kaizendeveloper.bitcoinsandbox.db.dao.UserDao
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import io.reactivex.Maybe
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val db: SandboxDatabase,
    private val userDao: UserDao
) {

    val users = userDao.getAll()
    val currentUser = userDao.getCurrent()

    fun insert(user: User) {
        doAsync { userDao.insert(user) }
    }

    fun getByName(name: String): Maybe<User> = userDao.getByName(name)

    fun updateCurrent(old: User, new: User) {
        doAsync {
            db.runInTransaction {
                userDao.update(old.apply { isCurrent = false })
                userDao.update(new.apply { isCurrent = true })
            }
        }
    }
}
