package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import com.kaizendeveloper.bitcoinsandbox.db.repository.UsersRepository
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput
import javax.inject.Inject

class UsersViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    utxoPoolRepository: UTXOPoolRepository
) : ViewModel() {

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
