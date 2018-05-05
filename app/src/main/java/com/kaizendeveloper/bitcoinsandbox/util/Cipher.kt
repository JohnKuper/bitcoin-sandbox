package com.kaizendeveloper.bitcoinsandbox.util

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec

object Cipher {

    private const val BOUNCY_CASTLE = "BC"

    fun generateECKeyPair(): KeyPair {
        val ecParamSpec = ECGenParameterSpec("secp256k1")
        val keyGen = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE).apply { initialize(ecParamSpec) }

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

    fun ripeMD160(input: ByteArray): ByteArray =
        MessageDigest.getInstance("RipeMD160", BouncyCastleProvider()).digest(input)

    private fun getECSignature() = Signature.getInstance("SHA256withECDSA")
}
