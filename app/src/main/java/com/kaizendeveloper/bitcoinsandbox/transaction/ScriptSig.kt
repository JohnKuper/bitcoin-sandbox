package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.ByteArrayWrapper
import java.io.OutputStream
import java.security.PublicKey
import java.util.Arrays

/**
 * Simple version of the real scriptSig for validating [TransactionInput]
 */
//TODO It's possible to replace PublicKey here with wrapper since only encoded is used
class ScriptSig(val signature: ByteArrayWrapper, val publicKey: PublicKey) {

    fun serialize(outputStream: OutputStream) {
        with(outputStream) {
            write(signature.data)
            write(publicKey.encoded)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScriptSig

        if (signature != other.signature) return false
        if (!Arrays.equals(publicKey.encoded, other.publicKey.encoded)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = signature.hashCode()
        result = 31 * result + publicKey.encoded.hashCode()
        return result
    }
}
