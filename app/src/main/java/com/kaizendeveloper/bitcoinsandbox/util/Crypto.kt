package com.kaizendeveloper.bitcoinsandbox.util

import java.security.MessageDigest
import java.security.PublicKey
import java.security.Signature

object Crypto {

    /**
     * @return true if [signature] is a valid digital signature of [message] under the [pubKey].
     */
    fun verifySignature(pubKey: PublicKey, message: ByteArray, signature: ByteArray): Boolean {
        val sig = Signature.getInstance("SHA256withECDSA")
        sig.initVerify(pubKey)
        sig.update(message)
        return sig.verify(signature)
    }

    fun computeSha256(input: ByteArray): ByteArray = MessageDigest.getInstance("SHA-256", "BC").digest(input)

    fun computeRipeMD160(input: ByteArray): ByteArray = MessageDigest.getInstance("RipeMD160", "BC").digest(input)
}
