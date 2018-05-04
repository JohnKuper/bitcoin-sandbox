package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.interfaces.ECPublicKey

class Block(val prevBlockHash: ByteArray?, address: PublicKey) {

    var hash: ByteArray? = null
        private set
    val coinbase: Transaction
    val transactions: ArrayList<Transaction>

    val rawBlock: ByteArray
        get() {
            val rawBlock = arrayListOf<Byte>()
            if (prevBlockHash != null)
                for (i in prevBlockHash.indices)
                    rawBlock.add(prevBlockHash[i])
            for (i in 0 until transactions.size) {
                val rawTx = transactions[i].getRawTx()
                for (j in rawTx.indices) {
                    rawBlock.add(rawTx[j])
                }
            }
            val raw = ByteArray(rawBlock.size)
            for (i in raw.indices)
                raw[i] = rawBlock.get(i)
            return raw
        }

    init {
        coinbase = Transaction(COINBASE, address as ECPublicKey)
        transactions = ArrayList()
    }

    fun getTransaction(index: Int): Transaction {
        return transactions[index]
    }

    fun addTransaction(tx: Transaction) {
        transactions.add(tx)
    }

    fun finalize() {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(rawBlock)
            hash = md.digest()
        } catch (x: NoSuchAlgorithmException) {
            x.printStackTrace(System.err)
        }

    }

    companion object {

        const val COINBASE = 25.0
    }
}