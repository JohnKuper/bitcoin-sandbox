package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.ByteArrayWrapper
import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import java.io.OutputStream

/**
 * Transaction input corresponding to the [TransactionOutput] with outputIndex [outputIndex] in the
 * transaction whose hash is [txHash]
 */
data class TransactionInput(
    val txHash: ByteArrayWrapper,
    val outputIndex: Int,
    var scriptSig: ScriptSig? = null
) {

    fun serializeToSign(outputStream: OutputStream) {
        with(outputStream) {
            write(txHash.data)
            write(outputIndex.toByteArray())
        }
    }

    fun serialize(outputStream: OutputStream) {
        with(outputStream) {
            serializeToSign(outputStream)
            scriptSig?.also { it.serialize(outputStream) }
        }
    }
}
