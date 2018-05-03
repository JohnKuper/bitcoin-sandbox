package com.kaizendeveloper.bitcoinsandbox.transaction

import java.util.Arrays


class UTXO
/**
 * Creates a new UTXO corresponding to the output with index <index> in the transaction whose
 * hash is `txHash`
</index> */
    (
    txHash: ByteArray,
    /** Index of the corresponding output in said transaction  */
    /** @return the index of this UTXO
     */
    val index: Int
) : Comparable<UTXO> {

    /** Hash of the transaction from which this UTXO originates  */
    /** @return the transaction hash of this UTXO
     */
    val txHash: ByteArray

    init {
        this.txHash = Arrays.copyOf(txHash, txHash.size)
    }

    /**
     * Compares this UTXO to the one specified by `other`, considering them equal if they have
     * `txHash` arrays with equal contents and equal `index` values
     */
    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (javaClass != other.javaClass) {
            return false
        }

        val utxo = other as UTXO?
        val hash = utxo!!.txHash
        val `in` = utxo.index
        if (hash.size != txHash.size || index != `in`)
            return false
        for (i in hash.indices) {
            if (hash[i] != txHash[i])
                return false
        }
        return true
    }

    /**
     * Simple implementation of a UTXO hashCode that respects equality of UTXOs // (i.e.
     * utxo1.equals(utxo2) => utxo1.hashCode() == utxo2.hashCode())
     */
    override fun hashCode(): Int {
        var hash = 1
        hash = hash * 17 + index
        hash = hash * 31 + Arrays.hashCode(txHash)
        return hash
    }

    /** Compares this UTXO to the one specified by `utxo`  */
    override fun compareTo(utxo: UTXO): Int {
        val hash = utxo.txHash
        val `in` = utxo.index
        if (`in` > index)
            return -1
        else if (`in` < index)
            return 1
        else {
            val len1 = txHash.size
            val len2 = hash.size
            if (len2 > len1)
                return -1
            else if (len2 < len1)
                return 1
            else {
                for (i in 0 until len1) {
                    if (hash[i] > txHash[i])
                        return -1
                    else if (hash[i] < txHash[i])
                        return 1
                }
                return 0
            }
        }
    }
}