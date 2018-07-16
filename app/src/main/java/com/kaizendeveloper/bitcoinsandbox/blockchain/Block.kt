package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import java.util.Arrays

class Block(
    val hash: ByteArray,
    val prevBlockHash: ByteArray,
    val merkleRoot: ByteArray,
    val timestamp: Long,
    val target: Long,
    val nonce: Int,
    val transactions: List<Transaction>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (!Arrays.equals(hash, other.hash)) return false
        if (!Arrays.equals(prevBlockHash, other.prevBlockHash)) return false
        if (!Arrays.equals(merkleRoot, other.merkleRoot)) return false
        if (timestamp != other.timestamp) return false
        if (target != other.target) return false
        if (nonce != other.nonce) return false
        if (transactions != other.transactions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(hash)
        result = 31 * result + Arrays.hashCode(prevBlockHash)
        result = 31 * result + Arrays.hashCode(merkleRoot)
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + nonce
        result = 31 * result + transactions.hashCode()
        return result
    }
}
