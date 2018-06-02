package com.kaizendeveloper.bitcoinsandbox.transaction

import android.arch.lifecycle.Observer
import com.kaizendeveloper.bitcoinsandbox.SandboxApplication
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import java.util.HashMap

object UTXOPool {

    private val utxoPoolRepository: UTXOPoolRepository = UTXOPoolRepository(SandboxApplication.application)

    private val initListeners = arrayListOf<OnInitListener>()

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

                initListeners.forEach { it.onInitializationCompleted() }
            }
        })
    }

    fun addInitListener(listener: OnInitListener) {
        initListeners.add(listener)
    }

    /**
     * Adds a mapping from UTXO [utxo] to transaction output [txOutput] to the pool
     */
    @JvmStatic
    fun add(utxo: UTXO, txOutput: TransactionOutput) {
        unspentOutputMap[utxo] = txOutput
        utxoPoolRepository.insert(utxo, txOutput)
    }

    /**
     * Removes the UTXO [utxo] from the pool
     */
    @JvmStatic
    fun remove(utxo: UTXO) {
        unspentOutputMap.remove(utxo)
        utxoPoolRepository.delete(utxo)
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

    interface OnInitListener {
        fun onInitializationCompleted()
    }
}
