package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.wrap
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TransferManager @Inject constructor(
    private val txHandler: TransactionHandler,
    private val utxoPoolRepo: UTXOPoolRepository
) {

    fun sendCoins(amount: Double, sender: User, recipient: User): Completable {
        return Single.fromCallable {
            if (sender.balance < amount) {
                throw IllegalStateException("Not enough coins!")
            }

            val txParams = prepareTransferParams(amount, sender)
            Transaction().apply {
                txParams.second.forEach {
                    addInput(it.txHash.data, it.outputIndex)
                }
                addOutput(amount, recipient.address)
                if (txParams.first > amount) {
                    addOutput(txParams.first - amount, sender.address)
                }

                val keyPair = Cipher.retrieveKeyPair(sender)
                inputs.forEachIndexed { index, _ ->
                    val inputSignature = Cipher.sign(getRawDataToSign(index), keyPair.private)
                    val scriptSig = ScriptSig(inputSignature.wrap(), keyPair.public)
                    addScriptSig(scriptSig, index)
                }

                build()
            }
        }.flatMapCompletable {
            txHandler.handle(it)
        }.subscribeOn(Schedulers.computation())
    }

    private fun prepareTransferParams(amount: Double, sender: User): Pair<Double, List<UTXO>> {
        var accumulatedAmount = 0.0
        val outputsToSpend = utxoPoolRepo.getPool()
            .asSequence()
            .filter { it.value.address == sender.address }
            .sortedBy { it.value }
            .takeWhile { accumulatedAmount <= amount }
            .onEach { accumulatedAmount += it.value.amount }
            .map { it.key }
            .toList()

        return Pair(accumulatedAmount, outputsToSpend)
    }
}
