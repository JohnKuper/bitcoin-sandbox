package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import java.io.OutputStream
import java.security.PublicKey

class TransactionInput(prevTxHash: ByteArray, val outputIndex: Int) {

    val prevTxHash: ByteArray = prevTxHash.copyOf()
    var scriptSig: ScriptSig? = null

    fun serializeToSign(outputStream: OutputStream) {
        with(outputStream) {
            write(prevTxHash)
            write(outputIndex.toByteArray())
        }
    }

    fun serialize(outputStream: OutputStream) {
        with(outputStream) {
            serializeToSign(outputStream)
            scriptSig?.also { it.serialize(outputStream) }
        }
    }

    /**
     * Simple version of the real scriptSig for validating [TransactionInput]
     */
    class ScriptSig(signature: ByteArray, val publicKey: PublicKey) {

        val signature = signature.copyOf()

        fun serialize(outputStream: OutputStream) {
            with(outputStream) {
                write(signature)
                write(publicKey.encoded)
            }
        }
    }
}
