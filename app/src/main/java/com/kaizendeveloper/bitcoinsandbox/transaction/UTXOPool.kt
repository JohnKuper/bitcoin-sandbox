package com.kaizendeveloper.bitcoinsandbox.transaction

import android.app.Application
import android.arch.lifecycle.Observer
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import java.util.HashMap

class UTXOPool(app: Application) {

    private val utxoPoolRepository: UTXOPoolRepository = UTXOPoolRepository(app)

    /**
     * The current collection of UTXOs, with each one mapped to its corresponding transaction output
     */
    val unspentOutputMap: HashMap<UTXO, TransactionOutput> = HashMap()

    //TODO Implement OneTimeObserver which removes itself after hitting once
    init {
        utxoPoolRepository.observableUTXOPool.observeForever(object : Observer<List<UTXOWithTxOutput>> {
            override fun onChanged(utxoPool: List<UTXOWithTxOutput>?) {
                utxoPool?.associateTo(unspentOutputMap) { it.utxo to it.txOutput }
                utxoPoolRepository.observableUTXOPool.removeObserver(this)
            }
        })
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

    fun getAllUTXO() = unspentOutputMap.keys.toList()

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
