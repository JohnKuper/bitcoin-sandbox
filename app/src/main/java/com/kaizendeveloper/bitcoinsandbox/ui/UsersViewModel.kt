package com.kaizendeveloper.bitcoinsandbox.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import com.kaizendeveloper.bitcoinsandbox.db.repository.UsersRepository
import com.kaizendeveloper.bitcoinsandbox.model.UserManager


class UsersViewModel(app: Application) : AndroidViewModel(app) {

    private val usersRepository: UsersRepository = UsersRepository(app)
    private val utxoPoolRepository: UTXOPoolRepository = UTXOPoolRepository(app)

    val observableUTXOPool: LiveData<List<UTXOWithTxOutput>> = utxoPoolRepository.observableUTXOPool
    val observableUsers: LiveData<List<User>> = usersRepository.observableUsers

    fun updateCurrentUserIfNeeded(old: User, new: User) {
        if (new != old) {
            usersRepository.updateCurrent(old, new)
        }
    }

    fun getUserBalance(user: User) = UserManager.getUserBalance(user)
}
