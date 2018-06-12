package com.kaizendeveloper.bitcoinsandbox.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.PURPOSE_SIGN
import android.security.keystore.KeyProperties.PURPOSE_VERIFY
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec

object Cipher {

    private const val ANDROID_KEY_STORE = "AndroidKeyStore"

    val zeroHash = ByteArray(32)
    val maxHash = ByteArray(32, { 0xFF.toByte() })

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

    fun verifySignature(pubKey: PublicKey, message: ByteArray, signature: ByteArray): Boolean {
        val sig = getECSignature().apply {
            initVerify(pubKey)
        }
        sig.update(message)
        return sig.verify(signature)
    }

    fun sign(input: ByteArray, privateKey: PrivateKey): ByteArray {
        val signature = getECSignature().apply {
            initSign(privateKey)
        }
        signature.update(input)
        return signature.sign()
    }

    fun sha256(input: ByteArray): ByteArray = MessageDigest.getInstance("SHA-256").digest(input)

    fun sha256Twice(input: ByteArray) = sha256(sha256(input))

    fun ripeMD160(input: ByteArray): ByteArray =
        MessageDigest.getInstance("RipeMD160", BouncyCastleProvider()).digest(input)

    fun retrieveKeyPair(user: User): KeyPair {
        val ks = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
            load(null)
        }
        val entry = ks.getEntry(user.name, null)
        val privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
        val publicKey = ks.getCertificate(user.name).publicKey

        return KeyPair(publicKey, privateKey)
    }

    fun decodePublicKey(encoded: ByteArray): PublicKey {
        val spec = X509EncodedKeySpec(encoded)
        val keyFactory = KeyFactory.getInstance("EC")

        return keyFactory.generatePublic(spec)
    }

    private fun getECSignature() = Signature.getInstance("SHA256withECDSA")
}
