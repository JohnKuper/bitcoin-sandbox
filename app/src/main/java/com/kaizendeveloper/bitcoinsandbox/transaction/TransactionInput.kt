package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import java.io.OutputStream
import java.security.PublicKey
import java.util.Arrays

class TransactionInput(prevTxHash: ByteArray, val outputIndex: Int) {

    val prevTxHash: ByteArray = Arrays.copyOf(prevTxHash, prevTxHash.size)
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

    class ScriptSig(signature: ByteArray, val publicKey: PublicKey) {

        val signature = Arrays.copyOf(signature, signature.size)

        fun serialize(outputStream: OutputStream) {
            with(outputStream) {
                write(signature)
                write(publicKey.encoded)
            }
        }
    }
}
