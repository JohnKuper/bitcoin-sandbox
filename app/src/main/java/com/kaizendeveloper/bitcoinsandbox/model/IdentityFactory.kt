package com.kaizendeveloper.bitcoinsandbox.model

import com.kaizendeveloper.bitcoinsandbox.util.Base58
import com.kaizendeveloper.bitcoinsandbox.util.Crypto
import com.kaizendeveloper.bitcoinsandbox.util.toHex
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec

private const val UNCOMPRESSED_PUBLIC_KEY_PREFIX = "04"
private const val BASE58_CHECKSUM_SIZE = 4
private const val HEX_RADIX = 16

object IdentityFactory {

    fun generateECKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("EC", "BC")
        val ecParamSpec = ECGenParameterSpec("secp256k1")
        keyGen.initialize(ecParamSpec)
        return keyGen.genKeyPair()
    }

    fun toAddress(pubKey: ECPublicKey): String {
        println("public key: x=${pubKey.w.affineX}, y=${pubKey.w.affineY}")
        val pkWithPrefix = encodeWithPrefix(pubKey)
        println("public key with prefix (HEX): $pkWithPrefix")

        val pkSha256 = Crypto.computeSha256(pkWithPrefix.toByteArray())
        println("public key SHA-256 (HEX): ${pkSha256.toHex()}")

        val rmd160 = Crypto.computeRipeMD160(pkSha256)
        println("public key RIPEMD160 (HEX): ${rmd160.toHex()}")

        val rmd160WithVersion = ByteArray(rmd160.size + 1).apply { this[0] = 0x00 }
        System.arraycopy(rmd160, 0, rmd160WithVersion, 1, rmd160.size)
        println("public key RIPEMD160 with version prefix (HEX) ${rmd160WithVersion.toHex()}")

        val rmdWithVersionSha2 = Crypto.computeSha256(rmd160WithVersion).let {
            Crypto.computeSha256(it)
        }

        val rmdWithCheckSum = ByteArray(rmd160WithVersion.size + BASE58_CHECKSUM_SIZE)
        System.arraycopy(rmd160WithVersion, 0, rmdWithCheckSum, 0, rmd160WithVersion.size)
        System.arraycopy(rmdWithVersionSha2, 0, rmdWithCheckSum, rmd160WithVersion.size, BASE58_CHECKSUM_SIZE)
        println("rmdWithCheckSum: ${rmdWithCheckSum.toHex()}")

        val address = Base58.encode(rmdWithCheckSum)
        println("BitCoin address: $address")
        return address
    }

    private fun encodeWithPrefix(pubKey: ECPublicKey): String {
        return with(pubKey) {
            val stringX = w.affineX.toString(HEX_RADIX)
            val stringY = w.affineY.toString(HEX_RADIX)
            "$UNCOMPRESSED_PUBLIC_KEY_PREFIX$stringX$stringY".toUpperCase()
        }
    }
}
