package com.kaizendeveloper.bitcoinsandbox.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.PURPOSE_SIGN
import android.security.keystore.KeyProperties.PURPOSE_VERIFY
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec

object Cipher {

    private const val ANDROID_KEY_STORE = "AndroidKeyStore"

    fun generateECKeyPair(alias: String): KeyPair {
        val keyGen = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, ANDROID_KEY_STORE).apply {
            initialize(
                KeyGenParameterSpec.Builder(alias, PURPOSE_SIGN or PURPOSE_VERIFY)
                    .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .build()
            )
        }

        return keyGen.genKeyPair()
    }

    //TODO Create keys directly in tests
    fun generateECKeyPair2(): KeyPair {
        val ecParamSpec = ECGenParameterSpec("secp256k1")
        val keyGen = KeyPairGenerator.getInstance("EC", "BC").apply { initialize(ecParamSpec) }

        return keyGen.generateKeyPair()
    }

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

    fun sha256(input: ByteArray): ByteArray = MessageDigest.getInstance("SHA-256").digest(input)

    fun sha256Twice(input: ByteArray) = sha256(sha256(input))

    fun ripeMD160(input: ByteArray): ByteArray =
        MessageDigest.getInstance("RipeMD160", BouncyCastleProvider()).digest(input)

    private fun getECSignature() = Signature.getInstance("SHA256withECDSA")
}
