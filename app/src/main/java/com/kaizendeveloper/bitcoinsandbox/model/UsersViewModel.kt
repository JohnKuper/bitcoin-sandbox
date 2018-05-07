package com.kaizendeveloper.bitcoinsandbox.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.kaizendeveloper.bitcoinsandbox.db.SandboxRepository
import com.kaizendeveloper.bitcoinsandbox.db.User


class UsersViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: SandboxRepository = SandboxRepository(app)
    val allUsers: LiveData<List<User>> = repository.allUsers

    fun insert(user: User) {
        repository.insert(user)
    }
}
