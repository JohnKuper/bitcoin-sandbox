package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.wrap
import javax.inject.Inject

class TxHandler @Inject constructor(
    private val utxoPool: UTXOPool,
    private val mempool: Mempool
) {

    /**
     * @return true if:
     * (1) all outputs claimed by [tx] are in the current [UTXOPool],
     * (2) the signatures on each input of [tx] are valid,
     * (3) no [UTXO] is claimed multiple times by [tx],
     * (4) all of [tx]s output values are non-negative, and
     * (5) the sum of [tx]s input values is greater than or equal to the sum of its output values; and false otherwise.
     */
    private fun isValidTx(tx: Transaction): Boolean {
        if (tx.isCoinbase) return true

        val utxoHashSet = hashSetOf<UTXO>()
        var inputSum = 0.0

        for ((index, input) in tx.inputs.withIndex()) {

            val utxo = UTXO.fromTxInput(input)
            if (!utxoPool.contains(utxo)) {
                return false
            }

            val txOutput = utxoPool.get(utxo)
            val scriptSig = input.scriptSig
            if (txOutput == null
                || scriptSig == null
                || !Cipher.verifySignature(scriptSig.publicKey, tx.getRawDataToSign(index), scriptSig.signature.data)
            ) {
                return false
            }

            if (utxoHashSet.contains(utxo)) {
                return false
            } else {
                utxoHashSet.add(utxo)
            }

            inputSum += txOutput.amount
        }

        return verifyAmounts(inputSum, tx.outputs)
    }

    private fun verifyAmounts(inputSum: Double, outputs: List<TransactionOutput>): Boolean {
        var outputSum = 0.0
        outputs.forEach {
            if (it.amount < 0.0) {
                return false
            }
            outputSum += it.amount
        }
        return inputSum >= outputSum
    }

    /**
     * Handles unordered array of proposed transactions, checking each transaction for correctness, returning a
     * mutually valid array of accepted transactions, and updating the current [UTXOPool] as appropriate.
     */

    fun handleTxs(possibleTxs: Array<Transaction>): Array<Transaction> {
        return possibleTxs
            .asSequence()
            .filter { isValidTx(it) }
            .onEach {
                it.inputs.forEach {
                    utxoPool.remove(UTXO.fromTxInput(it))
                }
                it.outputs.forEachIndexed { index, output ->
                    val newUtxo = UTXO(it.hash!!.wrap(), index)
                    utxoPool.add(newUtxo, output)
                }
                addToMempool(it)
            }.toList().toTypedArray()
    }

    private fun addToMempool(tx: Transaction) {
        if (tx.isCoinbase) {
            mempool.addCoinbase(tx)
        } else {
            mempool.add(tx)
        }
    }
}
