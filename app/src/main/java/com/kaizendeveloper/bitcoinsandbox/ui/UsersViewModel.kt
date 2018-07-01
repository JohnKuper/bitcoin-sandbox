package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import com.kaizendeveloper.bitcoinsandbox.db.repository.UsersRepository
import com.kaizendeveloper.bitcoinsandbox.model.UserManager
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO
import com.kaizendeveloper.bitcoinsandbox.util.requireValue
import javax.inject.Inject

class UsersViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val utxoPoolRepository: UTXOPoolRepository,
    private val userManager: UserManager
) : ViewModel() {

    val users: LiveData<List<User>> = usersRepository.users
    val currentUser: LiveData<User> = object : MediatorLiveData<User>() {

        private var lastUser: User? = null
        private var lastUTXOPool: HashMap<UTXO, TransactionOutput>? = null

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
                    balance = calculateBalance(user, utxoPool.values.toList())
                }
            }
        }
    }

    fun createUserIfAbsent(name: String) = userManager.createUserIfAbsent(name)

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

    //TODO Don't rely on live data for business logic
    fun getUserByAddress(address: String): User = users.requireValue().single { it.address == address }
}
