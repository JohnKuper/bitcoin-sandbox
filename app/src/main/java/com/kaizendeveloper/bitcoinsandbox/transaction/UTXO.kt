package com.kaizendeveloper.bitcoinsandbox.transaction

import java.util.Arrays

/**
 * Creates a new UTXO corresponding to the output with index [index] in the transaction whose hash is [txHash]
 */
class UTXO(val txHash: ByteArray, val index: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UTXO

        if (index != other.index) return false
        if (!Arrays.equals(txHash, other.txHash)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + Arrays.hashCode(txHash)
        return result
    }

    companion object {

        fun fromTxInput(txInput: TransactionInput) = UTXO(txInput.prevTxHash, txInput.outputIndex)
    }
}
