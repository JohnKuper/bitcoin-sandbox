package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.db.repository.MempoolRepository
import com.kaizendeveloper.bitcoinsandbox.util.observeOnce

class Mempool constructor(
    private val mempoolRepository: MempoolRepository
) {

    private val transactions = arrayListOf<Transaction>()

    init {
        mempoolRepository.transactions.observeOnce {
            it?.also {
                transactions.addAll(it)
            }
        }
    }

    fun add(tx: Transaction) {
        transactions.add(tx)
        mempoolRepository.insert(tx)
    }

    //TODO Excessive complexity. Delete this.
    fun addCoinbase(tx: Transaction) {
        transactions.add(0, tx)
        mempoolRepository.insert(tx)
    }

    fun getAll() = transactions
}
