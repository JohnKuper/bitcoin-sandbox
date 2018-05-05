package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.Cipher

class TxHandler {

    /**
     * @return true if:
     * (1) all outputs claimed by [tx] are in the current [UTXOPool],
     * (2) the signatures on each input of [tx] are valid,
     * (3) no [UTXO] is claimed multiple times by [tx],
     * (4) all of [tx]s output values are non-negative, and
     * (5) the sum of [tx]s input values is greater than or equal to the sum of its output values; and false otherwise.
     */
    fun isValidTx(tx: Transaction): Boolean {
        if (tx.coinbase) return true

        val utxoHashSet = hashSetOf<UTXO>()
        var inputSum = 0

        for ((index, input) in tx.inputs.withIndex()) {

            val utxo = UTXO.fromTxInput(input)
            if (!UTXOPool.contains(utxo)) {
                return false
            }

            val txOutput = UTXOPool.get(utxo)
            if (txOutput == null
                || input.signature == null
                || !Cipher.verifySignature(
                    txOutput.bitCoinPublicKey.publicKey,
                    tx.getRawDataToSign(index),
                    input.signature!!
                )
            ) {
                return false
            }

            if (utxoHashSet.contains(utxo)) {
                return false
            } else {
                utxoHashSet.add(utxo)
            }

            inputSum += txOutput.amount.toInt()
        }

        return verifyAmounts(inputSum, tx.outputs)
    }

    private fun verifyAmounts(inputSum: Int, outputs: List<Transaction.Output>): Boolean {
        var outputSum = 0
        outputs.forEach {
            if (it.amount < 0) {
                return false
            }
            outputSum += it.amount.toInt()
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
                    UTXOPool.remove(UTXO.fromTxInput(it))
                }
                it.outputs.forEachIndexed { index, output ->
                    val newUtxo = UTXO(it.hash!!, index)
                    UTXOPool.add(newUtxo, output)
                }
            }.toList().toTypedArray()
    }
}
