package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import com.kaizendeveloper.bitcoinsandbox.util.observeOnce
import java.util.HashMap

class UTXOPool constructor(
    private val utxoPoolRepository: UTXOPoolRepository
) {

    /**
     * The current collection of UTXOs, with each one mapped to its corresponding transaction output
     */
    val unspentOutputMap: HashMap<UTXO, TransactionOutput> = HashMap()

    init {
        utxoPoolRepository.utxoPool.observeOnce { utxoPool ->
            utxoPool?.associateTo(unspentOutputMap) { it.utxo to it.txOutput }
        }
    }

    /**
     * Adds a mapping from UTXO [utxo] to transaction output [txOutput] to the pool
     */
    fun add(utxo: UTXO, txOutput: TransactionOutput) {
        unspentOutputMap[utxo] = txOutput
        utxoPoolRepository.insert(utxo, txOutput)
    }

    /**
     * Removes the UTXO [utxo] from the pool
     */
    fun remove(utxo: UTXO) {
        unspentOutputMap.remove(utxo)
        utxoPoolRepository.delete(utxo)
    }

    /**
     * @return the transaction output corresponding to UTXO [utxo], or null if it's not in the pool.
     */
    fun get(utxo: UTXO): TransactionOutput? {
        return unspentOutputMap[utxo]
    }

    fun getAllTxOutputs() = unspentOutputMap.values.toList()

    fun reset() {
        unspentOutputMap.clear()
    }

    fun size() = unspentOutputMap.size

    /**
     * @return true if UTXO [utxo] is in the pool and false otherwise
     */
    operator fun contains(utxo: UTXO) = unspentOutputMap.containsKey(utxo)
}
