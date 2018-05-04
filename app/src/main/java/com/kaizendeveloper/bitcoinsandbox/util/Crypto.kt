package com.kaizendeveloper.bitcoinsandbox.util

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec

object Crypto {

    private const val BOUNCY_CASTLE = "BC"

    fun generateECKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE)
        val ecParamSpec = ECGenParameterSpec("secp256k1")
        keyGen.initialize(ecParamSpec)
        return keyGen.genKeyPair()
    }

    /**
     * @return true if [signature] is a valid digital signature of [message] under the [pubKey].
     */
    @JvmStatic
    fun verifySignature(pubKey: PublicKey, message: ByteArray, signature: ByteArray): Boolean {
        val sig = getECSignature().apply { initVerify(pubKey) }
        sig.update(message)
        return sig.verify(signature)
    }

    @JvmStatic
    fun sign(input: ByteArray, privateKey: PrivateKey): ByteArray {
        val signature = getECSignature().apply { initSign(privateKey) }
        signature.update(input)
        return signature.sign()
    }

    fun sha256(input: ByteArray): ByteArray = MessageDigest.getInstance("SHA-256", BOUNCY_CASTLE).digest(input)

    fun sha256Twice(input: ByteArray) = sha256(sha256(input))

    fun ripeMD160(input: ByteArray): ByteArray = MessageDigest.getInstance("RipeMD160", BOUNCY_CASTLE).digest(input)

    private fun getECSignature() = Signature.getInstance("SHA256withECDSA")
}
