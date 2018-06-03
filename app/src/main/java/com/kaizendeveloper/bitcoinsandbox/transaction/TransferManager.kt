package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.SandboxApplication
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.model.UserManager
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


class TransferManager {

    fun sendCoins(amount: Double, sender: User, recipient: User): Completable {
        return Completable.fromAction {
            sendCoinsInner(amount, sender, recipient)
        }.subscribeOn(Schedulers.computation())
    }

    //TODO Very heavy operation when amount of inputs is huge. Consider to add progress and block UI.
    private fun sendCoinsInner(amount: Double, sender: User, recipient: User) {
        val balance = UserManager.calculateBalance(sender)
        if (balance < amount) {
            throw IllegalStateException("Not enough coins!")
        }

        val txParams = prepareTransferParams(amount, sender)
        val transaction = Transaction().apply {
            txParams.second.forEach {
                addInput(it.txHash, it.outputIndex)
            }
            addOutput(amount, recipient.address)
            if (txParams.first > amount) {
                addOutput(txParams.first - amount, sender.address)
            }

            val keyPair = Cipher.retrieveKeyPair(sender)
            inputs.forEachIndexed { index, _ ->
                val inputSignature = Cipher.sign(getRawDataToSign(index), keyPair.private)
                val scriptSig = TransactionInput.ScriptSig(inputSignature, keyPair.public)
                addScriptSig(scriptSig, index)
            }

            build()
        }

        TxHandler().handleTxs(arrayOf(transaction))
    }

    private fun prepareTransferParams(amount: Double, sender: User): Pair<Double, List<UTXO>> {
        var accumulatedAmount = 0.0

        val outputsToSpend = SandboxApplication.utxoPool.unspentOutputMap
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
