package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.Cipher

class Block(val prevBlockHash: ByteArray? = null) {

    var hash: ByteArray? = null
        private set

    val transactions = arrayListOf<Transaction>()

    private fun toByteArray(): ByteArray {
        val rawBlock = arrayListOf<Byte>()
        prevBlockHash?.forEach { rawBlock.add(it) }
        transactions.forEach {
            rawBlock.addAll(it.getRawData().toList())
        }

        return rawBlock.toByteArray()
    }

    fun getTransaction(index: Int): Transaction = transactions[index]

    fun addTransaction(tx: Transaction) {
        transactions.add(tx)
    }

    fun build() {
        hash = Cipher.sha256(toByteArray())
    }
}
