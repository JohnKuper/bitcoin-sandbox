package com.kaizendeveloper.bitcoinsandbox.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.kaizendeveloper.bitcoinsandbox.SandboxApplication
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import com.kaizendeveloper.bitcoinsandbox.db.repository.UsersRepository
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput


class UsersViewModel(app: Application) : AndroidViewModel(app) {

    //TODO Use correct injections
    private val usersRepository: UsersRepository = SandboxApplication.application.usersRepo
    private val utxoPoolRepository: UTXOPoolRepository = SandboxApplication.application.utxoPoolRepository

    val users: LiveData<List<User>> = usersRepository.users
    val currentUser: LiveData<User> = object : MediatorLiveData<User>() {

        private var lastUser: User? = null
        private var lastUTXOPool: List<UTXOWithTxOutput>? = null

        init {
            addSource(usersRepository.currentUser, {
                lastUser = it
                considerNotifyUserDataChanged()
            })
            addSource(utxoPoolRepository.utxoPool, {
                lastUTXOPool = it
                considerNotifyUserDataChanged()
            })
        }

        private fun considerNotifyUserDataChanged() {
            val user = lastUser
            val utxoPool = lastUTXOPool
            if (user != null && utxoPool != null) {
                value = user.copy().apply {
                    balance = calculateBalance(user, utxoPool.map { it.txOutput })
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

    fun calculateBalance(user: User, utxoPool: List<TransactionOutput>): Double {
        return utxoPool.fold(0.0) { balance, output ->
            if (output.address == user.address) {
                balance + output.amount
            } else {
                balance
            }
        }
    }
}
