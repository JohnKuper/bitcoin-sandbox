package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.ByteArrayWrapper

/**
 * [UTXO] corresponding to the [TransactionOutput] with outputIndex [outputIndex] in the transaction
 * whose hash is [txHash]
 */
data class UTXO(val txHash: ByteArrayWrapper, val outputIndex: Int) {

    companion object {
        fun fromTxInput(txInput: TransactionInput) = UTXO(txInput.txHash, txInput.outputIndex)
    }
}
