package com.kaizendeveloper.bitcoinsandbox

import java.util.ArrayList
import java.util.HashMap

class UTXOPool {

    /**
     * The current collection of UTXOs, with each one mapped to its corresponding transaction output
     */
    private var unspentOutputMap: HashMap<UTXO, Transaction.Output>? = null

    /**
     * Returns an `ArrayList` of all UTXOs in the pool
     */
    val allUTXO: ArrayList<UTXO>
        get() {
            val setUTXO = unspentOutputMap!!.keys
            val allUTXO = ArrayList<UTXO>()
            allUTXO.addAll(setUTXO)
            return allUTXO
        }

    /**
     * Creates a new empty UTXOPool
     */
    constructor() {
        unspentOutputMap = HashMap<UTXO, Transaction.Output>()
    }

    /**
     * Creates a new UTXOPool that is a copy of `uPool`
     */
    constructor(uPool: UTXOPool) {
        unspentOutputMap = HashMap<UTXO, Transaction.Output>(uPool.unspentOutputMap)
    }

    /**
     * Adds a mapping from UTXO `utxo` to transaction output @code{txOut} to the pool
     */
    fun addUTXO(utxo: UTXO, txOut: Transaction.Output) {
        unspentOutputMap!![utxo] = txOut
    }

    /**
     * Removes the UTXO `utxo` from the pool
     */
    fun removeUTXO(utxo: UTXO) {
        unspentOutputMap!!.remove(utxo)
    }

    /**
     * @return the transaction output corresponding to UTXO `utxo`, or null if `utxo` is
     * not in the pool.
     */
    fun getTxOutput(ut: UTXO): Transaction.Output {
        return unspentOutputMap!![ut]!!
    }

    /**
     * @return true if UTXO `utxo` is in the pool and false otherwise
     */
    operator fun contains(utxo: UTXO): Boolean {
        return unspentOutputMap!!.containsKey(utxo)
    }
}
