package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.Crypto

class TxHandler(utxoPool: UTXOPool) {

    val utxoPool: UTXOPool =
        UTXOPool(utxoPool)

    /**
     * @return true if:
     * (1) all outputs claimed by `tx` are in the current scrooge.UTXO pool,
     * (2) the signatures on each input of `tx` are valid,
     * (3) no scrooge.UTXO is claimed multiple times by `tx`,
     * (4) all of `tx`s output values are non-negative, and
     * (5) the sum of `tx`s input values is greater than or equal to the sum of its output
     * values; and false otherwise.
     */
    fun isValidTx(tx: Transaction): Boolean {

        val utxoHashSet = hashSetOf<UTXO>()
        var index = 0
        var inputSum = 0
        for (input in tx.inputs) {
            if (input == null || input!!.signature == null) {
                return false
            }

            val utxo = UTXO(input!!.prevTxHash!!, input!!.outputIndex)
            if (!utxoPool.contains(utxo)) {
                return false
            }

            val output = utxoPool.getTxOutput(utxo)
            if (!Crypto.verifySignature(
                    output.address,
                    tx.getRawDataToSign(index)!!,
                    input!!.signature!!
                )
            ) {
                return false
            }
            index++

            if (utxoHashSet.contains(utxo)) {
                return false
            } else {
                utxoHashSet.add(utxo)
            }

            inputSum += output.amount.toInt()
        }

        var outputSum = 0
        for (output in tx.outputs) {
            if (output == null || output!!.amount < 0) {
                return false
            }
            outputSum += output!!.amount.toInt()
        }

        return inputSum >= outputSum
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */

    fun handleTxs(possibleTxs: Array<Transaction>?): Array<Transaction> {
        if (possibleTxs == null) {
            return emptyArray()
        }

        val validTxs = arrayListOf<Transaction>()
        for (tx in possibleTxs) {
            if (!isValidTx(tx)) {
                continue
            }
            validTxs.add(tx)

            for (input in tx.inputs) {
                val utxo = UTXO(input.prevTxHash!!, input.outputIndex)
                utxoPool.removeUTXO(utxo)
            }

            for (i in 0 until tx.outputs.size) {
                val newUtxo = UTXO(tx.hash!!, i)
                utxoPool.addUTXO(newUtxo, tx.outputs[i])
            }
        }

        return validTxs.toArray(arrayOfNulls<Transaction>(0))
    }
}