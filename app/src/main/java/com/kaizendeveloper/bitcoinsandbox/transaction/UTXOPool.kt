package com.kaizendeveloper.bitcoinsandbox.transaction

import java.util.HashMap

object UTXOPool {

    /**
     * The current collection of UTXOs, with each one mapped to its corresponding transaction output
     */
    private var unspentOutputMap: HashMap<UTXO, TransactionOutput> = HashMap()

    /**
     * Adds a mapping from UTXO [utxo] to transaction output [txOutput] to the pool
     */
    @JvmStatic
    fun add(utxo: UTXO, txOutput: TransactionOutput) {
        unspentOutputMap[utxo] = txOutput
    }

    /**
     * Removes the UTXO [utxo] from the pool
     */
    @JvmStatic
    fun remove(utxo: UTXO) {
        unspentOutputMap.remove(utxo)
    }

    /**
     * @return the transaction output corresponding to UTXO [utxo], or null if it's not in the pool.
     */
    @JvmStatic
    fun get(utxo: UTXO): TransactionOutput? {
        return unspentOutputMap[utxo]
    }

    @JvmStatic
    fun getAllUTXO() = unspentOutputMap.keys.toList()

    fun getAllTxOutputs() = unspentOutputMap.values.toList()

    @JvmStatic
    fun reset() {
        unspentOutputMap.clear()
    }

    fun size() = unspentOutputMap.size

    /**
     * @return true if UTXO [utxo] is in the pool and false otherwise
     */
    operator fun contains(utxo: UTXO) = unspentOutputMap.containsKey(utxo)
}
