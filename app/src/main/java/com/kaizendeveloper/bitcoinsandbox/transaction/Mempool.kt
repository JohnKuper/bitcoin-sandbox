package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.ByteArrayWrapper

class Mempool {

    private var hashToTransactionMap: HashMap<ByteArrayWrapper, Transaction>? = null

    val transactions: ArrayList<Transaction>
        get() {
            val transactions = arrayListOf<Transaction>()
            transactions.addAll(hashToTransactionMap!!.values)
            return transactions
        }

    constructor() {
        hashToTransactionMap = HashMap()
    }

    constructor(txPool: Mempool) {
        hashToTransactionMap = HashMap(txPool.hashToTransactionMap)
    }

    fun addTransaction(tx: Transaction) {
        val hash = ByteArrayWrapper(tx.hash!!)
        hashToTransactionMap!![hash] = tx
    }

    fun removeTransaction(txHash: ByteArray) {
        val hash = ByteArrayWrapper(txHash)
        hashToTransactionMap!!.remove(hash)
    }

    fun getTransaction(txHash: ByteArray): Transaction? {
        val hash = ByteArrayWrapper(txHash)
        return hashToTransactionMap!!.get(hash)
    }
}