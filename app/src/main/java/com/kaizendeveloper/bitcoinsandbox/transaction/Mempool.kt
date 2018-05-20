package com.kaizendeveloper.bitcoinsandbox.transaction

import android.arch.lifecycle.MutableLiveData

object Mempool {

    private val transactions = arrayListOf<Transaction>()
    val observableTransactions = MutableLiveData<List<Transaction>>()

    fun add(tx: Transaction) {
        transactions.add(tx)
        updateLiveData()
    }

    fun reset() {
        transactions.clear()
        updateLiveData()
    }

    fun getAll() = transactions

    private fun updateLiveData() {
        observableTransactions.postValue(transactions)
    }
}
