package com.kaizendeveloper.bitcoinsandbox.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.kaizendeveloper.bitcoinsandbox.db.SandboxRepository
import com.kaizendeveloper.bitcoinsandbox.db.User


class UsersViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: SandboxRepository = SandboxRepository(app)

    private val mutableCurrentUser: MutableLiveData<User> = MutableLiveData()
    val observableCurrentUser: LiveData<User>
        get() = mutableCurrentUser

    val observableUsers: LiveData<List<User>> = repository.observableUsers

    lateinit var currentUser: User

    init {
        observableUsers.observeForever(object : Observer<List<User>> {
            override fun onChanged(users: List<User>?) {
                currentUser = users?.first { it.isCurrent }!!
                mutableCurrentUser.value = currentUser
                observableUsers.removeObserver(this)
            }
        })
    }

    fun updateCurrentUserIfNeeded(newCurrent: User) {
        if (newCurrent != currentUser) {
            repository.updateCurrentUser(currentUser, newCurrent)
            mutableCurrentUser.value = newCurrent
            currentUser = newCurrent
        }
    }
}
