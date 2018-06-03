package com.kaizendeveloper.bitcoinsandbox.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import com.kaizendeveloper.bitcoinsandbox.db.repository.UsersRepository
import com.kaizendeveloper.bitcoinsandbox.model.UserManager


class UsersViewModel(app: Application) : AndroidViewModel(app) {

    private val usersRepository: UsersRepository = UsersRepository(app)
    private val utxoPoolRepository: UTXOPoolRepository = UTXOPoolRepository(app)

    val users: LiveData<List<User>> = usersRepository.users
    val currentUser: LiveData<User> = object : MediatorLiveData<User>() {

        private var lastUser: User? = null
        private var lastUTXOPool: List<UTXOWithTxOutput>? = null

        init {
            addSource(usersRepository.currentUser, {
                lastUser = it
                considerNotify()
            })
            addSource(utxoPoolRepository.utxoPool, {
                lastUTXOPool = it
                considerNotify()
            })
        }

        private fun considerNotify() {
            val user = lastUser
            val utxoPool = lastUTXOPool
            if (user != null && utxoPool != null) {
                value = user.copy().apply {
                    balance = UserManager.calculateBalance(user, utxoPool.map { it.txOutput })
                }
            }
        }
    }

    fun updateCurrentUserIfNeeded(newCurrent: User) {
        val oldUser = currentUser.value
        if (oldUser != null && oldUser != newCurrent) {
            usersRepository.updateCurrent(oldUser, newCurrent)
        }
    }
}
