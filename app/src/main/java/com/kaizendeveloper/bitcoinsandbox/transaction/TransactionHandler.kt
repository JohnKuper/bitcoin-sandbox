package com.kaizendeveloper.bitcoinsandbox.transaction

import android.support.annotation.VisibleForTesting
import com.kaizendeveloper.bitcoinsandbox.db.repository.MempoolRepository
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TransactionHandler @Inject constructor(
    private val utxoPoolRepo: UTXOPoolRepository,
    private val mempoolRepo: MempoolRepository
) {

    /**
     * @return true if:
     * (1) all outputs claimed by [tx] are in the current utxo pool,
     * (2) the signatures on each input of [tx] are valid,
     * (3) no [UTXO] is claimed multiple times by [tx],
     * (4) all of [tx]s output values are non-negative, and
     * (5) the sum of [tx]s input values is greater than or equal to the sum of its output values; and false otherwise.
     */
    @VisibleForTesting
    fun isValidTx(tx: Transaction): Boolean {
        if (tx.isCoinbase) return true

        val utxoHashSet = hashSetOf<UTXO>()
        var inputSum = 0.0

        for ((index, input) in tx.inputs.withIndex()) {

            val utxo = UTXO.fromTxInput(input)
            val utxoPool = utxoPoolRepo.getPool()
            if (!utxoPool.contains(utxo)) {
                return false
            }

            val txOutput = utxoPool[utxo]
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
     * Handles proposed transaction and updates current utxo pool and mempool if it's a valid.
     */
    fun handle(transaction: Transaction): Completable {
        return Single.just(transaction)
            .filter { isValidTx(it) }
            .flatMapCompletable {
                utxoPoolRepo.updatePool(it).andThen(mempoolRepo.insert(it))
            }.subscribeOn(Schedulers.computation())
    }
}
