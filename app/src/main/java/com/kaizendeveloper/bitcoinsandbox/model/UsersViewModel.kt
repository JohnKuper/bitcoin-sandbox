package com.kaizendeveloper.bitcoinsandbox.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.kaizendeveloper.bitcoinsandbox.db.SandboxRepository
import com.kaizendeveloper.bitcoinsandbox.db.User


class UsersViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: SandboxRepository = SandboxRepository(app)

    val observableUsers: LiveData<List<User>> = repository.observableUsers
    val observableCurrentUser: LiveData<User> = repository.observableCurrentUser

    lateinit var currentUser: User

    init {
        observableCurrentUser.observeForever {
            it?.also { currentUser = it }
        }
    }

    fun updateCurrentUserIfNeeded(newCurrent: User) {
        if (newCurrent != currentUser) {
            repository.updateCurrentUser(currentUser, newCurrent)
        }
    }
}
