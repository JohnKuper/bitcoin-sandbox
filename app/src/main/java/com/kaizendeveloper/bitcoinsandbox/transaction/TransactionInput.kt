package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.ByteArrayWrapper
import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import java.io.OutputStream

data class TransactionInput(val prevTxHash: ByteArrayWrapper, val outputIndex: Int) {

    var scriptSig: ScriptSig? = null

    fun serializeToSign(outputStream: OutputStream) {
        with(outputStream) {
            write(prevTxHash.data)
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
